package ma.fsbm.lias_lab.controller;

import ma.fsbm.lias_lab.entity.Equipe;
import ma.fsbm.lias_lab.service.EquipeService;
import ma.fsbm.lias_lab.service.MandatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/equipes")
public class EquipeController {

    @Autowired
    private EquipeService equipeService;

    @Autowired
    private MandatService mandatService;

    @GetMapping
    public String listEquipes(Model model) {
        model.addAttribute("equipesActives", equipeService.getEquipesActives());
        model.addAttribute("equipesArchivees", equipeService.getEquipesArchivees());
        return "equipes/liste";
    }

    @GetMapping("/ajouter")
    public String formAjout(Model model) {
        model.addAttribute("equipe", new Equipe());
        return "equipes/formulaire";
    }

    @PostMapping("/enregistrer")
    public String enregistrer(@ModelAttribute Equipe equipe) {
        equipeService.saveEquipe(equipe);
        return "redirect:/equipes";
    }

    // Section 21 : remplace /equipes/supprimer/{id}
    @PostMapping("/archiver/{id}")
    public String archiver(@PathVariable Long id) {
        equipeService.archiverEquipe(id);
        return "redirect:/equipes";
    }

    @PostMapping("/desarchiver/{id}")
    public String desarchiver(@PathVariable Long id) {
        equipeService.desarchiverEquipe(id);
        return "redirect:/equipes";
    }

    // Détail d'une équipe avec ses membres et son chef d'équipe actuel
    @GetMapping("/{id}")
    public String detailEquipe(@PathVariable Long id, Model model) {
        model.addAttribute("equipe", equipeService.getEquipeById(id));
        model.addAttribute("chefEquipe", mandatService.getChefEquipeActif(id));
        return "equipes/detail";
    }
}