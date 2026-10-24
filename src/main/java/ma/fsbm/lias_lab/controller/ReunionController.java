package ma.fsbm.lias_lab.controller;

import ma.fsbm.lias_lab.entity.Membre;
import ma.fsbm.lias_lab.entity.Reunion;
import ma.fsbm.lias_lab.service.EquipeService;
import ma.fsbm.lias_lab.service.MembreService;
import ma.fsbm.lias_lab.service.ReunionService;
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
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/reunions")
public class ReunionController {

    @Autowired
    private ReunionService reunionService;

    @Autowired
    private EquipeService equipeService;

    @Autowired
    private MembreService membreService;

    private static final String DOSSIER_PV = "uploads/pv/";

    @GetMapping
    public String listReunions(Model model) {
        model.addAttribute("reunionsActives", reunionService.getReunionsActives());
        model.addAttribute("reunionsArchivees", reunionService.getReunionsArchivees());
        return "reunions/liste";
    }

    @GetMapping("/ajouter")
    public String formAjout(Model model) {
        model.addAttribute("reunion", new Reunion());
        model.addAttribute("equipes", equipeService.getEquipesActives());
        model.addAttribute("membres", membreService.getAllMembres());
        return "reunions/formulaire";
    }

    @PostMapping("/enregistrer")
    public String enregistrer(@ModelAttribute Reunion reunion,
                               @RequestParam(value = "participantIds", required = false) List<Long> participantIds) {
        if (participantIds != null && !participantIds.isEmpty()) {
            List<Membre> participants = membreService.getMembresByIds(participantIds);
            reunion.setParticipants(participants);
        }
        reunionService.creerReunion(reunion);
        return "redirect:/reunions";
    }

    @GetMapping("/{id}")
    public String detailReunion(@PathVariable Long id, Model model) {
        model.addAttribute("reunion", reunionService.getReunionById(id));
        return "reunions/detail";
    }

    @PostMapping("/{id}/uploader-pv")
    public String uploaderPv(@PathVariable Long id, @RequestParam("pv") MultipartFile pv) throws IOException {
        if (pv != null && !pv.isEmpty()) {
            Files.createDirectories(Paths.get(DOSSIER_PV));
            String nomFichier = UUID.randomUUID() + "_" + pv.getOriginalFilename();
            Files.copy(pv.getInputStream(), Paths.get(DOSSIER_PV + nomFichier), StandardCopyOption.REPLACE_EXISTING);
            reunionService.attacherPv(id, nomFichier);
        }
        return "redirect:/reunions/" + id;
    }

    @GetMapping("/{id}/telecharger-pv")
    public ResponseEntity<Resource> telechargerPv(@PathVariable Long id) {
        Reunion reunion = reunionService.getReunionById(id);
        Resource fichier = new FileSystemResource(DOSSIER_PV + reunion.getCheminPv());

        String extension = "";
        String cheminStocke = reunion.getCheminPv();
        int pointIndex = cheminStocke.lastIndexOf('.');
        if (pointIndex > 0) {
            extension = cheminStocke.substring(pointIndex);
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"PV_" + reunion.getTitre() + extension + "\"")
                .body(fichier);
    }

    @PostMapping("/archiver/{id}")
    public String archiver(@PathVariable Long id) {
        reunionService.archiverReunion(id);
        return "redirect:/reunions";
    }

    @PostMapping("/desarchiver/{id}")
    public String desarchiver(@PathVariable Long id) {
        reunionService.desarchiverReunion(id);
        return "redirect:/reunions";
    }
}