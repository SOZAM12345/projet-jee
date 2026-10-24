package ma.fsbm.lias_lab.controller;

import ma.fsbm.lias_lab.service.HistoriqueAffiliationService;
import ma.fsbm.lias_lab.service.MembreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/membres/{membreId}/historique")
public class HistoriqueAffiliationController {

    @Autowired
    private HistoriqueAffiliationService historiqueService;

    @Autowired
    private MembreService membreService;

    @GetMapping
    public String listHistorique(@PathVariable Long membreId, Model model) {
        model.addAttribute("membre", membreService.getMembreById(membreId));
        model.addAttribute("historique", historiqueService.getHistoriqueByMembre(membreId));
        return "historique/liste";
    }
}