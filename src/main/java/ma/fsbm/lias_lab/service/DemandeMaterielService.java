package ma.fsbm.lias_lab.service;

import ma.fsbm.lias_lab.entity.*;
import ma.fsbm.lias_lab.repository.DemandeMaterielRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DemandeMaterielService {

    @Autowired
    private DemandeMaterielRepository demandeRepository;

    @Autowired
    private MembreService membreService;

    @Autowired
    private MandatService mandatService;

    @Autowired
    private NotificationService notificationService;

    public List<DemandeMateriel> getDemandesEnAttente() {
        return demandeRepository.findByStatut(StatutDemandeMateriel.EN_ATTENTE);
    }

    public List<DemandeMateriel> getAllDemandes() {
        return demandeRepository.findAll();
    }

    // Section 15 : "Soumission des demandes"
    public DemandeMateriel soumettreDemande(DemandeMateriel demande) {
        return demandeRepository.save(demande);
    }

    // Section 6/15 : même règle que pour les demandes d'adhésion — seul le directeur en mandat valide
    public List<Membre> getDirecteursActifs() {
        return mandatService.getMembresAvecMandatActif(TypeRole.DIRECTEUR);
    }

    private void verifierEstDirecteurActif(Long directeurId) {
        boolean estDirecteurActif = getDirecteursActifs().stream()
                .anyMatch(m -> m.getId().equals(directeurId));
        if (!estDirecteurActif) {
            throw new IllegalStateException("Seul le directeur en mandat peut valider une demande de matériel.");
        }
    }

    // Section 15 : "Validation par direction"
    public void validerDemande(Long id, Long directeurId) {
        verifierEstDirecteurActif(directeurId);
        DemandeMateriel demande = demandeRepository.findById(id).orElseThrow();
        Membre directeur = membreService.getMembreById(directeurId);

        demande.setStatut(StatutDemandeMateriel.VALIDEE);
        demande.setDateDecision(LocalDateTime.now());
        demande.setValidateur(directeur);
        demandeRepository.save(demande);

        // Section 19 : notifier le demandeur
        notificationService.creerNotification(
                demande.getDemandeur(),
                "Votre demande de matériel a été validée",
                TypeNotification.VALIDATION_DEMANDE,
                "/demandes-materiel/mes-demandes"
        );
    }

    public void refuserDemande(Long id, Long directeurId) {
        verifierEstDirecteurActif(directeurId);
        DemandeMateriel demande = demandeRepository.findById(id).orElseThrow();
        Membre directeur = membreService.getMembreById(directeurId);

        demande.setStatut(StatutDemandeMateriel.REFUSEE);
        demande.setDateDecision(LocalDateTime.now());
        demande.setValidateur(directeur);
        demandeRepository.save(demande);

        // Section 19 : notifier le demandeur
        notificationService.creerNotification(
                demande.getDemandeur(),
                "Votre demande de matériel a été refusée",
                TypeNotification.VALIDATION_DEMANDE,
                "/demandes-materiel/mes-demandes"
        );
    }
}