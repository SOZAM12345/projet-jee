package ma.fsbm.lias_lab.controller;

import ma.fsbm.lias_lab.entity.DemandeAdhesion;
import ma.fsbm.lias_lab.entity.StatutMembre;
import ma.fsbm.lias_lab.service.DemandeAdhesionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Controller
@RequestMapping("/demandes")
public class DemandeAdhesionController {

    @Autowired
    private DemandeAdhesionService demandeService;

    private static final String DOSSIER_CV = "uploads/cv/";

    @GetMapping
    public String listDemandes(Model model) {
        model.addAttribute("demandes", demandeService.getDemandesEnAttente());
        model.addAttribute("directeurs", demandeService.getDirecteursActifs());
        model.addAttribute("statuts", StatutMembre.values());
        return "demandes/liste";
    }

    @GetMapping("/soumettre")
    public String formSoumettre(Model model) {
        model.addAttribute("demande", new DemandeAdhesion());
        return "demandes/formulaire";
    }

    @PostMapping("/enregistrer")
    public String enregistrerDemande(@ModelAttribute DemandeAdhesion demande,
                                      @RequestParam("cv") MultipartFile cv) throws IOException {
        if (cv != null && !cv.isEmpty()) {
            Files.createDirectories(Paths.get(DOSSIER_CV));
            String nomFichier = UUID.randomUUID() + "_" + cv.getOriginalFilename();
            Files.copy(cv.getInputStream(), Paths.get(DOSSIER_CV + nomFichier), StandardCopyOption.REPLACE_EXISTING);
            demande.setCheminCv(nomFichier);
        }
        demandeService.saveDemande(demande);
        return "redirect:/demandes/merci";
    }

    @GetMapping("/merci")
    public String merci() {
        return "demandes/merci";
    }

    @PostMapping("/accepter/{id}")
    public String accepter(@PathVariable Long id,
                            @RequestParam Long directeurId,
                            @RequestParam StatutMembre statutChoisi) {
        demandeService.accepterDemande(id, directeurId, statutChoisi);
        return "redirect:/demandes";
    }

    @PostMapping("/refuser/{id}")
    public String refuser(@PathVariable Long id, @RequestParam Long directeurId) {
        demandeService.refuserDemande(id, directeurId);
        return "redirect:/demandes";
    }

    @GetMapping("/statut")
    public String formStatut() {
        return "demandes/statut";
    }

    @PostMapping("/statut")
    public String consulterStatut(@RequestParam String email, Model model) {
        model.addAttribute("email", email);
        demandeService.getDerniereDemandeParEmail(email)
                .ifPresentOrElse(
                        d -> model.addAttribute("demande", d),
                        () -> model.addAttribute("erreur", "Aucune demande trouvée pour cet email.")
                );
        return "demandes/statut";
    }
}