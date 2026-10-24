package ma.fsbm.lias_lab.controller;

import ma.fsbm.lias_lab.entity.ActiviteConvention;
import ma.fsbm.lias_lab.entity.Convention;
import ma.fsbm.lias_lab.service.ConventionService;
import ma.fsbm.lias_lab.service.PartenaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Controller
@RequestMapping("/conventions")
public class ConventionController {

    @Autowired
    private ConventionService conventionService;

    @Autowired
    private PartenaireService partenaireService;

    private static final String DOSSIER_CONVENTIONS = "uploads/conventions/";

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("conventionsActives", conventionService.getConventionsActives());
        model.addAttribute("conventionsArchivees", conventionService.getConventionsArchivees());
        return "conventions/liste";
    }

    @GetMapping("/ajouter")
    public String formAjout(Model model) {
        model.addAttribute("convention", new Convention());
        model.addAttribute("partenaires", partenaireService.getPartenairesActifs());
        return "conventions/formulaire";
    }

    @PostMapping("/enregistrer")
    public String enregistrer(@ModelAttribute Convention convention) {
        conventionService.creerConvention(convention);
        return "redirect:/conventions";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("convention", conventionService.getConventionById(id));
        model.addAttribute("activites", conventionService.getActivites(id));
        model.addAttribute("activite", new ActiviteConvention());
        return "conventions/detail";
    }

    @PostMapping("/{id}/uploader-document")
    public String uploaderDocument(@PathVariable Long id, @RequestParam("document") MultipartFile document) throws IOException {
        if (document != null && !document.isEmpty()) {
            Files.createDirectories(Paths.get(DOSSIER_CONVENTIONS));
            String nomFichier = UUID.randomUUID() + "_" + document.getOriginalFilename();
            Files.copy(document.getInputStream(), Paths.get(DOSSIER_CONVENTIONS + nomFichier), StandardCopyOption.REPLACE_EXISTING);
            conventionService.attacherDocument(id, nomFichier);
        }
        return "redirect:/conventions/" + id;
    }

    @GetMapping("/{id}/telecharger-document")
    public ResponseEntity<Resource> telechargerDocument(@PathVariable Long id) {
        Convention convention = conventionService.getConventionById(id);
        Resource fichier = new FileSystemResource(DOSSIER_CONVENTIONS + convention.getCheminDocument());

        String extension = "";
        int pointIndex = convention.getCheminDocument().lastIndexOf('.');
        if (pointIndex > 0) {
            extension = convention.getCheminDocument().substring(pointIndex);
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + convention.getTitre() + extension + "\"")
                .body(fichier);
    }

    // Section 16 : "Suivi des activités associées"
    @PostMapping("/{id}/ajouter-activite")
    public String ajouterActivite(@PathVariable Long id, @ModelAttribute ActiviteConvention activite) {
        Convention convention = conventionService.getConventionById(id);
        activite.setConvention(convention);
        conventionService.ajouterActivite(activite);
        return "redirect:/conventions/" + id;
    }

    @PostMapping("/archiver/{id}")
    public String archiver(@PathVariable Long id) {
        conventionService.archiverConvention(id);
        return "redirect:/conventions";
    }

    @PostMapping("/desarchiver/{id}")
    public String desarchiver(@PathVariable Long id) {
        conventionService.desarchiverConvention(id);
        return "redirect:/conventions";
    }
}