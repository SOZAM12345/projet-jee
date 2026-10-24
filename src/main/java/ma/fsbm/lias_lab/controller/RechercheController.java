package ma.fsbm.lias_lab.controller;

import ma.fsbm.lias_lab.entity.*;
import ma.fsbm.lias_lab.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;

@Controller
public class RechercheController {

    @Autowired
    private MembreService membreService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private EvenementService evenementService;

    @Autowired
    private PublicationService publicationService;

    // Section 18 : "Recherche globale : Membres, Documents, Événements, Publications"
    @GetMapping("/recherche")
    public String rechercher(@RequestParam(required = false) String q, Model model) {
        ResultatRecherche resultat = new ResultatRecherche();
        resultat.setMotCle(q);

        if (q == null || q.isBlank()) {
            resultat.setMembres(Collections.emptyList());
            resultat.setDocuments(Collections.emptyList());
            resultat.setEvenements(Collections.emptyList());
            resultat.setPublications(Collections.emptyList());
        } else {
            String motCle = q.toLowerCase();

            List<Membre> membres = membreService.getAllMembres().stream()
                    .filter(m -> (m.getNom() + " " + m.getPrenom()).toLowerCase().contains(motCle))
                    .toList();
            resultat.setMembres(membres);

            resultat.setDocuments(documentService.rechercher(q));

            List<Evenement> evenements = evenementService.getEvenementsActifs();
            evenements.addAll(evenementService.getEvenementsArchives());
            resultat.setEvenements(evenements.stream()
                    .filter(e -> e.getTitre() != null && e.getTitre().toLowerCase().contains(motCle))
                    .toList());

            List<Publication> publications = publicationService.getPublicationsActives().stream()
                    .filter(p -> p.getTitre() != null && p.getTitre().toLowerCase().contains(motCle))
                    .toList();
            resultat.setPublications(publications);
        }

        model.addAttribute("resultat", resultat);
        return "recherche/resultats";
    }
}