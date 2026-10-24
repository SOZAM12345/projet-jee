package ma.fsbm.lias_lab.controller;

import ma.fsbm.lias_lab.entity.RapportAnnuel;
import ma.fsbm.lias_lab.service.RapportAnnuelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/rapport-annuel")
public class RapportAnnuelController {

    @Autowired
    private RapportAnnuelService rapportAnnuelService;

    @GetMapping
    public String formulaire(Model model) {
        model.addAttribute("anneeCourante", LocalDate.now().getYear());
        return "rapport-annuel/formulaire";
    }

    @GetMapping("/generer")
    public String generer(@RequestParam int annee, Model model) {
        RapportAnnuel rapport = rapportAnnuelService.genererRapport(annee);
        model.addAttribute("rapport", rapport);
        return "rapport-annuel/rapport";
    }
}