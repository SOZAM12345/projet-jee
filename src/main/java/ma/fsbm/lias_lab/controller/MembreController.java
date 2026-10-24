package ma.fsbm.lias_lab.controller;

import ma.fsbm.lias_lab.entity.Membre;
import ma.fsbm.lias_lab.entity.StatutMembre;
import ma.fsbm.lias_lab.service.EquipeService;
import ma.fsbm.lias_lab.service.HistoriqueAffiliationService;
import ma.fsbm.lias_lab.service.MembreService;
import ma.fsbm.lias_lab.service.PublicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Controller
@RequestMapping("/membres")
public class MembreController {

    @Autowired
    private MembreService membreService;

    @Autowired
    private PublicationService publicationService;

    @Autowired
    private EquipeService equipeService;

    @Autowired
    private HistoriqueAffiliationService historiqueAffiliationService;

    private static final String DOSSIER_PHOTO = "uploads/photos/";

    @GetMapping
    public String listMembres(Model model) {
        model.addAttribute("membres", membreService.getAllMembres());
        return "membres/liste";
    }

    @GetMapping("/ajouter")
    public String formAjoutMembre(Model model) {
        model.addAttribute("membre", new Membre());
        model.addAttribute("equipes", equipeService.getEquipesActives());
        return "membres/formulaire";
    }

    @GetMapping("/modifier/{id}")
    public String formModifierMembre(@PathVariable Long id, Model model) {
        model.addAttribute("membre", membreService.getMembreById(id));
        model.addAttribute("equipes", equipeService.getEquipesActives());
        return "membres/formulaire";
    }

    @GetMapping("/{id}")
    public String voirProfil(@PathVariable Long id, Model model) {
        model.addAttribute("membre", membreService.getMembreById(id));
        model.addAttribute("historique", membreService.getHistoriqueModifications(id));
        model.addAttribute("publications", publicationService.getPublicationsParAuteur(id));
        return "membres/profil";
    }

    @PostMapping("/enregistrer")
    public String enregistrerMembre(@ModelAttribute Membre membre,
                                     @RequestParam(value = "equipeId", required = false) Long equipeId,
                                     @RequestParam(value = "photo", required = false) MultipartFile photo) throws IOException {
        Membre sauvegarde = (membre.getId() == null)
                ? membreService.creerMembre(membre)
                : membreService.modifierMembre(membre.getId(), membre);

        membreService.affecterEquipe(sauvegarde.getId(), equipeId);

        if (photo != null && !photo.isEmpty()) {
            Files.createDirectories(Paths.get(DOSSIER_PHOTO));
            String nomFichier = UUID.randomUUID() + "_" + photo.getOriginalFilename();
            Files.copy(photo.getInputStream(), Paths.get(DOSSIER_PHOTO + nomFichier), StandardCopyOption.REPLACE_EXISTING);
            membreService.setCheminPhoto(sauvegarde.getId(), nomFichier);
        }

        return "redirect:/membres/" + sauvegarde.getId();
    }

    @PostMapping("/desactiver/{id}")
    public String desactiver(@PathVariable Long id) {
        membreService.desactiverMembre(id);
        return "redirect:/membres";
    }

    @PostMapping("/reactiver/{id}")
    public String reactiver(@PathVariable Long id, @RequestParam StatutMembre nouveauStatut) {
        membreService.reactiverMembre(id, nouveauStatut);
        return "redirect:/membres";
    }

    @PostMapping("/retraite/{id}")
    public String mettreEnRetraite(@PathVariable Long id) {
        membreService.mettreEnRetraite(id);
        return "redirect:/membres";
    }

    // Section 4.3 : historique complet des périodes d'affiliation du membre
    @GetMapping("/{id}/historique")
    public String historiqueAffiliation(@PathVariable Long id, Model model) {
        model.addAttribute("membre", membreService.getMembreById(id));
        model.addAttribute("periodes", historiqueAffiliationService.getHistoriqueByMembre(id));
        return "membres/historique";
    }
}