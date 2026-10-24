package ma.fsbm.lias_lab.controller;

import ma.fsbm.lias_lab.entity.Materiel;
import ma.fsbm.lias_lab.entity.Membre;
import ma.fsbm.lias_lab.entity.TypeNotification;
import ma.fsbm.lias_lab.service.MaterielService;
import ma.fsbm.lias_lab.service.MembreService;
import ma.fsbm.lias_lab.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/materiel")
public class MaterielController {

    @Autowired
    private MaterielService materielService;

    @Autowired
    private MembreService membreService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("materielsActifs", materielService.getMaterielsActifs());
        model.addAttribute("materielsArchives", materielService.getMaterielsArchives());
        return "materiel/liste";
    }

    // Section 14 : "Qui a reçu quoi" vu du côté du membre — accessible à tout membre actif
    @GetMapping("/mes-distributions")
    public String mesDistributions(Authentication authentication, Model model) {
        Membre moi = membreService.getMembreByEmail(authentication.getName());
        model.addAttribute("distributions", materielService.getDistributionsDuMembre(moi.getId()));

        // Section 19 : la visite de la page marque comme lues les notifs "matériel reçu"
        notificationService.marquerLuesParType(moi, TypeNotification.MATERIEL_RECU);
        model.addAttribute("nbNotifMaterielRecu", notificationService.compterNonLusParType(moi, TypeNotification.MATERIEL_RECU));

        return "materiel/mes-distributions";
    }

    @GetMapping("/ajouter")
    public String formAjout(Model model) {
        model.addAttribute("materiel", new Materiel());
        return "materiel/formulaire";
    }

    @PostMapping("/enregistrer")
    public String enregistrer(@ModelAttribute Materiel materiel) {
        materielService.creerMateriel(materiel);
        return "redirect:/materiel";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Materiel materiel = materielService.getMaterielById(id);
        model.addAttribute("materiel", materiel);
        model.addAttribute("distributions", materielService.getDistributions(id));
        model.addAttribute("quantiteDistribuee", materielService.getQuantiteDistribuee(id));
        model.addAttribute("membresSansDistribution", materielService.getMembresSansDistribution(id));
        // Modifié : seuls les membres éligibles (actifs, ni retraités ni anciens)
        model.addAttribute("membres", materielService.getMembresEligibles());
        return "materiel/detail";
    }

    @PostMapping("/{id}/distribuer")
    public String distribuer(@PathVariable Long id,
                              @RequestParam Long membreId,
                              @RequestParam int quantite) {
        Materiel materiel = materielService.getMaterielById(id);
        materielService.distribuer(id, membreId, quantite);

        // Section 19 : notifie le membre qui reçoit le matériel
        Membre beneficiaire = membreService.getMembreById(membreId);
        if (beneficiaire != null && materiel != null) {
            notificationService.creerNotification(
                    beneficiaire,
                    "Vous avez reçu : " + materiel.getNom() + " (x" + quantite + ")",
                    TypeNotification.MATERIEL_RECU,
                    "/materiel/mes-distributions"
            );
        }

        return "redirect:/materiel/" + id;
    }

    @PostMapping("/archiver/{id}")
    public String archiver(@PathVariable Long id) {
        materielService.archiverMateriel(id);
        return "redirect:/materiel";
    }

    @PostMapping("/desarchiver/{id}")
    public String desarchiver(@PathVariable Long id) {
        materielService.desarchiverMateriel(id);
        return "redirect:/materiel";
    }
}