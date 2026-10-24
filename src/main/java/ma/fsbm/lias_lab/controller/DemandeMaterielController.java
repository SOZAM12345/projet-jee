package ma.fsbm.lias_lab.controller;

import ma.fsbm.lias_lab.entity.DemandeMateriel;
import ma.fsbm.lias_lab.entity.Membre;
import ma.fsbm.lias_lab.entity.TypeNotification;
import ma.fsbm.lias_lab.service.DemandeMaterielService;
import ma.fsbm.lias_lab.service.MembreService;
import ma.fsbm.lias_lab.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/demandes-materiel")
public class DemandeMaterielController {

    @Autowired
    private DemandeMaterielService demandeMaterielService;

    @Autowired
    private MembreService membreService;

    @Autowired
    private NotificationService notificationService;

    // Retrouve le membre connecté à partir de son email (username Spring Security)
    private Membre membreConnecte(Authentication auth) {
        return membreService.getAllMembres().stream()
                .filter(m -> m.getEmail().equalsIgnoreCase(auth.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Membre introuvable : " + auth.getName()));
    }

    // Liste des demandes en attente (membres internes ; boutons de décision réservés au directeur)
    @GetMapping
    public String liste(Model model) {
        model.addAttribute("demandes", demandeMaterielService.getDemandesEnAttente());
        return "demandes-materiel/liste";
    }

    @GetMapping("/soumettre")
    public String formSoumettre(Model model) {
        model.addAttribute("demande", new DemandeMateriel());
        return "demandes-materiel/formulaire";
    }

    @PostMapping("/enregistrer")
    public String enregistrer(@ModelAttribute DemandeMateriel demande, Authentication auth) {
        demande.setDemandeur(membreConnecte(auth));
        demandeMaterielService.soumettreDemande(demande);
        return "redirect:/demandes-materiel/mes-demandes";
    }

    // Les demandes du membre connecté (accessible à tous les membres actifs, doctorants inclus)
    @GetMapping("/mes-demandes")
    public String mesDemandes(Authentication auth, Model model) {
        Membre courant = membreConnecte(auth);
        List<DemandeMateriel> mesDemandes = demandeMaterielService.getAllDemandes().stream()
                .filter(d -> d.getDemandeur() != null
                        && d.getDemandeur().getId().equals(courant.getId()))
                .toList();
        model.addAttribute("demandes", mesDemandes);

        // Section 19 : la visite de "mes demandes" marque comme lues les notifs de validation/refus
        notificationService.marquerLuesParType(courant, TypeNotification.VALIDATION_DEMANDE);
        model.addAttribute("nbNotifValidationDemande", notificationService.compterNonLusParType(courant, TypeNotification.VALIDATION_DEMANDE));

        return "demandes-materiel/mes-demandes";
    }

    @PostMapping("/valider/{id}")
    public String valider(@PathVariable Long id, Authentication auth) {
        DemandeMateriel demande = trouverParId(id);
        demandeMaterielService.validerDemande(id, membreConnecte(auth).getId());

        // Section 19 : notifie le demandeur de la validation
        if (demande != null && demande.getDemandeur() != null) {
            notificationService.creerNotification(
                    demande.getDemandeur(),
                    "Votre demande de matériel (" + demande.getDesignation() + ") a été validée.",
                    TypeNotification.VALIDATION_DEMANDE,
                    "/demandes-materiel/mes-demandes"
            );
        }

        return "redirect:/demandes-materiel";
    }

    @PostMapping("/refuser/{id}")
    public String refuser(@PathVariable Long id, Authentication auth) {
        DemandeMateriel demande = trouverParId(id);
        demandeMaterielService.refuserDemande(id, membreConnecte(auth).getId());

        // Section 19 : notifie le demandeur du refus
        if (demande != null && demande.getDemandeur() != null) {
            notificationService.creerNotification(
                    demande.getDemandeur(),
                    "Votre demande de matériel (" + demande.getDesignation() + ") a été refusée.",
                    TypeNotification.VALIDATION_DEMANDE,
                    "/demandes-materiel/mes-demandes"
            );
        }

        return "redirect:/demandes-materiel";
    }

    @GetMapping("/historique")
    public String historique(Model model) {
        model.addAttribute("demandes", demandeMaterielService.getAllDemandes());
        return "demandes-materiel/historique";
    }

    private DemandeMateriel trouverParId(Long id) {
        return demandeMaterielService.getAllDemandes().stream()
                .filter(d -> d.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}