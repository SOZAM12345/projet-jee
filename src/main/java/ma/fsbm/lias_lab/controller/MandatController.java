package ma.fsbm.lias_lab.controller;

import ma.fsbm.lias_lab.entity.Mandat;
import ma.fsbm.lias_lab.entity.Membre;
import ma.fsbm.lias_lab.service.EquipeService;
import ma.fsbm.lias_lab.service.MandatService;
import ma.fsbm.lias_lab.service.MembreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/mandats")
public class MandatController {

    @Autowired
    private MandatService mandatService;

    @Autowired
    private MembreService membreService;

    @Autowired
    private EquipeService equipeService;

    @GetMapping
    public String listMandats(Model model) {
        model.addAttribute("mandatsGouvernance", mandatService.getMandatsGouvernance());
        model.addAttribute("mandatsEquipe", mandatService.getMandatsEquipe());
        return "mandats/liste";
    }

    @GetMapping("/ajouter")
    public String formAjoutMandat(Model model) {
        model.addAttribute("mandat", new Mandat());
        model.addAttribute("membres", membreService.getAllMembres());
        model.addAttribute("equipes", equipeService.getAllEquipes());
        return "mandats/formulaire";
    }

    @PostMapping("/enregistrer")
    public String enregistrerMandat(@ModelAttribute Mandat mandat,
                                     @RequestParam(value = "dateDebutStr", required = false) String dateDebutStr,
                                     @RequestParam(value = "dateFinStr", required = false) String dateFinStr) {
        if (dateDebutStr != null && !dateDebutStr.isBlank()) {
            mandat.setDateDebut(LocalDate.parse(dateDebutStr));
        }
        if (dateFinStr != null && !dateFinStr.isBlank()) {
            mandat.setDateFin(LocalDate.parse(dateFinStr));
        }
        mandatService.creerMandat(mandat);
        return "redirect:/mandats";
    }

    @PostMapping("/cloturer/{id}")
    public String cloturerMandat(@PathVariable Long id) {
        mandatService.cloturerMandat(id);
        return "redirect:/mandats";
    }

    @GetMapping("/membre/{id}")
    public String historiqueParMembre(@PathVariable Long id, Model model) {
        Membre membre = membreService.getMembreById(id);
        model.addAttribute("membre", membre);
        model.addAttribute("mandats", mandatService.getMandatsByMembre(id));
        return "mandats/historique";
    }
}