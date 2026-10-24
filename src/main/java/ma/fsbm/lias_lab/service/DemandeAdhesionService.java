package ma.fsbm.lias_lab.service;

import ma.fsbm.lias_lab.entity.*;
import ma.fsbm.lias_lab.repository.DemandeAdhesionRepository;
import ma.fsbm.lias_lab.repository.MembreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DemandeAdhesionService {

    @Autowired
    private DemandeAdhesionRepository demandeRepository;

    @Autowired
    private MembreRepository membreRepository;

    @Autowired
    private HistoriqueAffiliationService historiqueService;

    @Autowired
    private MandatService mandatService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<DemandeAdhesion> getAllDemandes() {
        return demandeRepository.findAll();
    }

    public List<DemandeAdhesion> getDemandesEnAttente() {
        return demandeRepository.findByStatut(StatutDemande.EN_ATTENTE);
    }

    public DemandeAdhesion saveDemande(DemandeAdhesion demande) {
        return demandeRepository.save(demande);
    }

    // Consultation publique : le candidat retrouve sa demande par email
    public Optional<DemandeAdhesion> getDerniereDemandeParEmail(String email) {
        return demandeRepository.findAll().stream()
                .filter(d -> d.getEmail().equalsIgnoreCase(email))
                .max((a, b) -> a.getDateSoumission().compareTo(b.getDateSoumission()));
    }

    // Section 6 : liste des membres habilités à valider — utilisée par le controller
    // pour ne proposer que les vrais directeurs en mandat dans le formulaire
    public List<Membre> getDirecteursActifs() {
        return mandatService.getMembresAvecMandatActif(TypeRole.DIRECTEUR);
    }

    private void verifierEstDirecteurActif(Long directeurId) {
        boolean estDirecteurActif = getDirecteursActifs().stream()
                .anyMatch(m -> m.getId().equals(directeurId));
        if (!estDirecteurActif) {
            throw new IllegalStateException(
                "Seul le directeur en mandat peut valider une demande d'adhésion (section 6 du cahier des charges).");
        }
    }

    // Accepter : crée un compte Membre avec le statut choisi par le directeur + ouvre son historique
    public void accepterDemande(Long id, Long directeurId, StatutMembre statutChoisi) {
        verifierEstDirecteurActif(directeurId);

        DemandeAdhesion demande = demandeRepository.findById(id).orElseThrow();
        Membre directeur = membreRepository.findById(directeurId).orElseThrow();

        boolean emailExiste = membreRepository.findAll().stream()
                .anyMatch(m -> m.getEmail().equalsIgnoreCase(demande.getEmail()));

        if (!emailExiste) {
            Membre membre = new Membre();
            membre.setNom(demande.getNom());
            membre.setPrenom(demande.getPrenom());
            membre.setEmail(demande.getEmail());
            membre.setMotDePasse(passwordEncoder.encode("changeme123"));
            membre.setStatut(statutChoisi);
            membre.setDateAffiliation(LocalDate.now());
            membreRepository.save(membre);

            historiqueService.ouvrirPeriode(membre, LocalDate.now(), "Adhésion initiale (demande acceptée)");
        }

        demande.setStatut(StatutDemande.ACCEPTEE);
        demande.setDateDecision(LocalDateTime.now());
        demande.setValidateur(directeur);
        demandeRepository.save(demande);
    }

    public void refuserDemande(Long id, Long directeurId) {
        verifierEstDirecteurActif(directeurId);

        DemandeAdhesion demande = demandeRepository.findById(id).orElseThrow();
        Membre directeur = membreRepository.findById(directeurId).orElseThrow();

        demande.setStatut(StatutDemande.REFUSEE);
        demande.setDateDecision(LocalDateTime.now());
        demande.setValidateur(directeur);
        demandeRepository.save(demande);
    }
}