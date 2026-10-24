package ma.fsbm.lias_lab.controller;

import ma.fsbm.lias_lab.entity.Publication;
import ma.fsbm.lias_lab.service.EquipeService;
import ma.fsbm.lias_lab.service.MembreService;
import ma.fsbm.lias_lab.service.PublicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/publications")
public class PublicationController {

    @Autowired
    private PublicationService publicationService;

    @Autowired
    private MembreService membreService;

    @Autowired
    private EquipeService equipeService;

    @GetMapping
    public String listPublications(
            @RequestParam(required = false) Long auteurId,
            @RequestParam(required = false) Integer annee,
            @RequestParam(required = false) Long equipeId,
            Model model) {

        model.addAttribute("publicationsActives", publicationService.rechercher(auteurId, annee, equipeId));
        model.addAttribute("publicationsArchivees", publicationService.getPublicationsArchivees());
        model.addAttribute("auteurId", auteurId);
        model.addAttribute("annee", annee);
        model.addAttribute("equipeId", equipeId);
        model.addAttribute("membres", membreService.getAllMembres());
        model.addAttribute("equipes", equipeService.getEquipesActives());
        return "publications/liste";
    }

    @GetMapping("/ajouter")
    public String formAjout(Model model) {
        model.addAttribute("publication", new Publication());
        model.addAttribute("membres", membreService.getAllMembres());
        model.addAttribute("equipes", equipeService.getEquipesActives());
        return "publications/formulaire";
    }

    @PostMapping("/enregistrer")
    public String enregistrer(@ModelAttribute Publication publication) {
        publicationService.savePublication(publication);
        return "redirect:/publications";
    }

    // Section 21 : remplace /publications/supprimer/{id}
    @PostMapping("/archiver/{id}")
    public String archiver(@PathVariable Long id) {
        publicationService.archiverPublication(id);
        return "redirect:/publications";
    }

    @PostMapping("/desarchiver/{id}")
    public String desarchiver(@PathVariable Long id) {
        publicationService.desarchiverPublication(id);
        return "redirect:/publications";
    }
}