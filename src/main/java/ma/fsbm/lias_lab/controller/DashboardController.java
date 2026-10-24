package ma.fsbm.lias_lab.controller;

import ma.fsbm.lias_lab.entity.Evenement;
import ma.fsbm.lias_lab.entity.Membre;
import ma.fsbm.lias_lab.repository.MembreRepository;
import ma.fsbm.lias_lab.service.DemandeAdhesionService;
import ma.fsbm.lias_lab.service.EvenementService;
import ma.fsbm.lias_lab.service.PublicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    @Autowired
    private MembreRepository membreRepository;

    @Autowired
    private PublicationService publicationService;

    @Autowired
    private EvenementService evenementService;

    @Autowired
    private DemandeAdhesionService demandeAdhesionService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {

        String email = authentication.getName();
        Membre membre = membreRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Membre introuvable : " + email));
        model.addAttribute("membre", membre);

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        boolean estDirecteur = roles.contains("ROLE_DIRECTEUR");
        boolean estViceDirecteur = roles.contains("ROLE_VICE_DIRECTEUR");
        boolean estChefEquipe = roles.contains("ROLE_CHEF_EQUIPE");
        boolean estDoctorant = roles.contains("ROLE_DOCTORANT");
        boolean estAssocie = roles.contains("ROLE_ASSOCIE");
        boolean estPermanent = roles.contains("ROLE_PERMANENT");

        model.addAttribute("estDirecteur", estDirecteur);
        model.addAttribute("estViceDirecteur", estViceDirecteur);
        model.addAttribute("estChefEquipe", estChefEquipe);
        model.addAttribute("estDoctorant", estDoctorant);
        model.addAttribute("estAssocie", estAssocie);
        model.addAttribute("estPermanent", estPermanent);

        // §3 : publications du membre connecté — tout statut y a droit (consultation au minimum)
        model.addAttribute("mesPublications", publicationService.getPublicationsParAuteur(membre.getId()));

        // §9 : les 5 prochains événements actifs, triés par date
        List<Evenement> evenements = evenementService.getEvenementsActifs();
        model.addAttribute("prochainsEvenements",
                evenements.size() > 5 ? evenements.subList(0, 5) : evenements);

        // §6 : bloc Direction — demandes d'adhésion en attente, réservé au directeur en mandat
        if (estDirecteur) {
            model.addAttribute("demandesEnAttente", demandeAdhesionService.getDemandesEnAttente());
        }

        return "dashboard";
    }
}