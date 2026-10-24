package ma.fsbm.lias_lab.service;

import ma.fsbm.lias_lab.entity.Evenement;
import ma.fsbm.lias_lab.entity.Membre;
import ma.fsbm.lias_lab.entity.TypeNotification;
import ma.fsbm.lias_lab.repository.EvenementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EvenementService {

    @Autowired
    private EvenementRepository evenementRepository;

    @Autowired
    private MembreService membreService;

    @Autowired
    private NotificationService notificationService;

    public List<Evenement> getEvenementsActifs() {
        return evenementRepository.findByArchiveFalseOrderByDateDebutAsc();
    }

    public List<Evenement> getEvenementsArchives() {
        return evenementRepository.findByArchiveTrueOrderByDateDebutDesc();
    }

    public Evenement getEvenementById(Long id) {
        return evenementRepository.findById(id).orElse(null);
    }

    // Section 9 : retrouve les autres éditions du même événement récurrent
    public List<Evenement> getAutresEditions(String titre) {
        return evenementRepository.findByTitreIgnoreCaseOrderByEditionDesc(titre);
    }

    public Evenement saveEvenement(Evenement evenement) {
        boolean estNouveau = (evenement.getId() == null);
        Evenement enregistre = evenementRepository.save(evenement);

        // Section 19 : notifier tous les membres uniquement à la création (pas à la modification)
        if (estNouveau) {
            List<Membre> membres = membreService.getAllMembres();
            notificationService.creerNotificationPourPlusieurs(
                    membres,
                    "Nouvel événement : " + enregistre.getTitre(),
                    TypeNotification.EVENEMENT,
                    "/evenements/" + enregistre.getId()
            );
        }

        return enregistre;
    }

    // Section 21 : remplace l'ancien DELETE physique — l'événement reste consultable
    public void archiverEvenement(Long id) {
        Evenement e = evenementRepository.findById(id).orElseThrow();
        e.setArchive(true);
        evenementRepository.save(e);

        // Section 19 : supprime les notifications "Nouvel événement" liées à cet événement
        notificationService.supprimerParLien("/evenements/" + id);
    }

    public void desarchiverEvenement(Long id) {
        Evenement e = evenementRepository.findById(id).orElseThrow();
        e.setArchive(false);
        evenementRepository.save(e);
    }
}