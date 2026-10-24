package ma.fsbm.lias_lab.controller;

import ma.fsbm.lias_lab.entity.Partenaire;
import ma.fsbm.lias_lab.service.ConventionService;
import ma.fsbm.lias_lab.service.PartenaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/partenaires")
public class PartenaireController {

    @Autowired
    private PartenaireService partenaireService;

    @Autowired
    private ConventionService conventionService;

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("partenairesActifs", partenaireService.getPartenairesActifs());
        model.addAttribute("partenairesArchives", partenaireService.getPartenairesArchives());
        return "partenaires/liste";
    }

    @GetMapping("/ajouter")
    public String formAjout(Model model) {
        model.addAttribute("partenaire", new Partenaire());
        return "partenaires/formulaire";
    }

    @PostMapping("/enregistrer")
    public String enregistrer(@ModelAttribute Partenaire partenaire) {
        partenaireService.creerPartenaire(partenaire);
        return "redirect:/partenaires";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("partenaire", partenaireService.getPartenaireById(id));
        model.addAttribute("conventions", conventionService.getConventionsParPartenaire(id));
        return "partenaires/detail";
    }

    @PostMapping("/archiver/{id}")
    public String archiver(@PathVariable Long id) {
        partenaireService.archiverPartenaire(id);
        return "redirect:/partenaires";
    }

    @PostMapping("/desarchiver/{id}")
    public String desarchiver(@PathVariable Long id) {
        partenaireService.desarchiverPartenaire(id);
        return "redirect:/partenaires";
    }
}