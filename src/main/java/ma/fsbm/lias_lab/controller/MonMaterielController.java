package ma.fsbm.lias_lab.controller;

import ma.fsbm.lias_lab.entity.DistributionMateriel;
import ma.fsbm.lias_lab.entity.Membre;
import ma.fsbm.lias_lab.repository.MembreRepository;
import ma.fsbm.lias_lab.service.MaterielService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MonMaterielController {

    @Autowired
    private MembreRepository membreRepository;

    @Autowired
    private MaterielService materielService;

    // §14 : chaque membre consulte ce qu'il a reçu (accessible à tous les membres connectés)
    @GetMapping("/mon-materiel")
    public String monMateriel(Authentication authentication, Model model) {
        String email = authentication.getName();
        Membre membre = membreRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Membre introuvable : " + email));

        List<DistributionMateriel> distributions =
                materielService.getDistributionsDuMembre(membre.getId());

        model.addAttribute("distributions", distributions);
        model.addAttribute("totalRecu",
                distributions.stream().mapToInt(DistributionMateriel::getQuantite).sum());
        return "mon-materiel";
    }
}