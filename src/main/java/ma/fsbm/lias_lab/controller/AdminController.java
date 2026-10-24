package ma.fsbm.lias_lab.controller;

import ma.fsbm.lias_lab.entity.Membre;
import ma.fsbm.lias_lab.entity.ParametreLaboratoire;
import ma.fsbm.lias_lab.entity.StatutMembre;
import ma.fsbm.lias_lab.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private MembreService membreService;

    @Autowired
    private PublicationService publicationService;

    @Autowired
    private EvenementService evenementService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private ReunionService reunionService;

    @Autowired
    private EquipeService equipeService;

    // Section 5.1 : "Comptes utilisateurs"
    @GetMapping("/comptes")
    public String comptes(Model model) {
        model.addAttribute("membres", membreService.getAllMembres());
        model.addAttribute("section", "comptes");
        return "admin/comptes";
    }

    @PostMapping("/comptes/{id}/reinitialiser")
    public String reinitialiser(@PathVariable Long id, Model model) {
        String nouveauMotDePasse = adminService.reinitialiserMotDePasse(id);
        model.addAttribute("membres", membreService.getAllMembres());
        model.addAttribute("motDePasseGenere", nouveauMotDePasse);
        model.addAttribute("membreIdConcerne", id);
        model.addAttribute("section", "comptes");
        return "admin/comptes";
    }

    // Création manuelle d'un compte par l'administrateur système
    @GetMapping("/comptes/nouveau")
    public String formulaireNouveauCompte(Model model) {
        model.addAttribute("nouveauMembre", new Membre());
        model.addAttribute("statuts", StatutMembre.values());
        model.addAttribute("equipes", equipeService.getEquipesActives());
        model.addAttribute("section", "comptes");
        return "admin/nouveau-compte";
    }

    @PostMapping("/comptes/nouveau")
    public String creerNouveauCompte(@ModelAttribute("nouveauMembre") Membre membre) {
        membre.setActif(true);
        membreService.creerMembre(membre);
        return "redirect:/admin/comptes";
    }

    // Section 5.1 : "Paramétrage"
    @GetMapping("/parametrage")
    public String parametrage(Model model) {
        model.addAttribute("parametres", adminService.getParametres());
        model.addAttribute("section", "parametrage");
        return "admin/parametrage";
    }

    @PostMapping("/parametrage/enregistrer")
    public String enregistrerParametrage(@ModelAttribute ParametreLaboratoire parametres) {
        adminService.sauvegarderParametres(parametres);
        return "redirect:/admin/parametrage";
    }

    // Section 5.1 : "Maintenance" — état du système
    @GetMapping("/maintenance")
    public String maintenance(Model model) {
        model.addAttribute("nbMembres", membreService.getAllMembres().size());
        model.addAttribute("nbPublications", publicationService.getPublicationsActives().size());
        model.addAttribute("nbEvenements", evenementService.getEvenementsActifs().size());
        model.addAttribute("nbDocuments", documentService.getDocumentsActifs().size());
        model.addAttribute("nbReunions", reunionService.getReunionsActives().size());
        model.addAttribute("section", "maintenance");
        return "admin/maintenance";
    }
}