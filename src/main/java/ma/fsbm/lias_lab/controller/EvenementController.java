package ma.fsbm.lias_lab.controller;

import ma.fsbm.lias_lab.entity.Evenement;
import ma.fsbm.lias_lab.entity.Membre;
import ma.fsbm.lias_lab.entity.TypeNotification;
import ma.fsbm.lias_lab.service.EvenementService;
import ma.fsbm.lias_lab.service.MembreService;
import ma.fsbm.lias_lab.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/evenements")
public class EvenementController {

    @Autowired
    private EvenementService evenementService;

    @Autowired
    private MembreService membreService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public String listEvenements(Model model, Authentication authentication) {
        model.addAttribute("evenementsActifs", evenementService.getEvenementsActifs());
        model.addAttribute("evenementsArchives", evenementService.getEvenementsArchives());

        // Section 19 : la visite de la liste marque comme lues les notifs "nouvel événement"
        if (authentication != null) {
            Membre membre = membreService.getMembreByEmail(authentication.getName());
            if (membre != null) {
                notificationService.marquerLuesParType(membre, TypeNotification.EVENEMENT);
                // Le badge du ControllerAdvice a été calculé avant cette ligne : on écrase
                // l'attribut avec la valeur fraîche pour qu'il disparaisse sans rafraîchir
                model.addAttribute("nbNotifEvenements", notificationService.compterNonLusParType(membre, TypeNotification.EVENEMENT));
            }
        }

        return "evenements/liste";
    }

    @GetMapping("/ajouter")
    public String formAjout(Model model) {
        model.addAttribute("evenement", new Evenement());
        model.addAttribute("membres", membreService.getAllMembres());
        return "evenements/formulaire";
    }

    @GetMapping("/{id}")
    public String voirDetail(@PathVariable Long id, Model model) {
        Evenement evenement = evenementService.getEvenementById(id);
        model.addAttribute("evenement", evenement);
        model.addAttribute("autresEditions", evenementService.getAutresEditions(evenement.getTitre()));
        return "evenements/detail";
    }

    @PostMapping("/enregistrer")
    public String enregistrer(@ModelAttribute Evenement evenement) {
        boolean estNouveau = evenement.getId() == null;
        Evenement sauvegarde = evenementService.saveEvenement(evenement);

        // Section 19 : notifie tous les membres actifs lors de la création d'un nouvel événement
        if (estNouveau) {
            for (Membre m : membreService.getAllMembres()) {
                if (m.isActif()) {
                    notificationService.creerNotification(
                            m,
                            "Nouvel événement : " + sauvegarde.getTitre(),
                            TypeNotification.EVENEMENT,
                            "/evenements/" + sauvegarde.getId()
                    );
                }
            }
        }

        return "redirect:/evenements";
    }

    // Section 21 : remplace /evenements/supprimer/{id}
    @PostMapping("/archiver/{id}")
    public String archiver(@PathVariable Long id) {
        evenementService.archiverEvenement(id);
        return "redirect:/evenements";
    }

    @PostMapping("/desarchiver/{id}")
    public String desarchiver(@PathVariable Long id) {
        evenementService.desarchiverEvenement(id);
        return "redirect:/evenements";
    }
}