package ma.fsbm.lias_lab.controller;

import ma.fsbm.lias_lab.entity.Document;
import ma.fsbm.lias_lab.entity.Membre;
import ma.fsbm.lias_lab.entity.TypeDocument;
import ma.fsbm.lias_lab.entity.TypeNotification;
import ma.fsbm.lias_lab.service.DocumentService;
import ma.fsbm.lias_lab.service.EvenementService;
import ma.fsbm.lias_lab.service.MembreService;
import ma.fsbm.lias_lab.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.UUID;

@Controller
@RequestMapping("/documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private EvenementService evenementService;

    @Autowired
    private MembreService membreService;

    @Autowired
    private NotificationService notificationService;

    private static final String DOSSIER_DOCS = "uploads/documents/";

    @GetMapping
    public String listDocuments(@RequestParam(required = false) String q, Model model, Authentication authentication) {
        model.addAttribute("documentsActifs", documentService.rechercher(q));
        model.addAttribute("documentsArchives", documentService.getDocumentsArchives());
        model.addAttribute("q", q);

        // Section 19 : la visite de la liste marque comme lues les notifs "nouveau document"
        if (authentication != null) {
            Membre membre = membreService.getMembreByEmail(authentication.getName());
            if (membre != null) {
                notificationService.marquerLuesParType(membre, TypeNotification.DOCUMENT);
                model.addAttribute("nbNotifDocuments", notificationService.compterNonLusParType(membre, TypeNotification.DOCUMENT));
            }
        }

        return "documents/liste";
    }

    @GetMapping("/ajouter")
    public String formAjout(Model model) {
        model.addAttribute("document", new Document());
        model.addAttribute("types", TypeDocument.values());
        model.addAttribute("evenements", evenementService.getEvenementsActifs());
        return "documents/formulaire";
    }

    @PostMapping("/enregistrer")
    public String enregistrer(@ModelAttribute Document document,
                               @RequestParam("fichier") MultipartFile fichier,
                               Authentication authentication) throws IOException {
        if (fichier != null && !fichier.isEmpty()) {
            Files.createDirectories(Paths.get(DOSSIER_DOCS));
            String nomFichier = UUID.randomUUID() + "_" + fichier.getOriginalFilename();
            Files.copy(fichier.getInputStream(), Paths.get(DOSSIER_DOCS + nomFichier), StandardCopyOption.REPLACE_EXISTING);
            document.setCheminFichier(nomFichier);
        }

        // Section 21 : traçabilité — l'uploadeur est TOUJOURS déduit du compte connecté,
        // jamais saisi manuellement, pour empêcher qu'un membre s'attribue un dépôt à un autre
        Membre uploader = null;
        if (authentication != null) {
            uploader = membreService.getMembreByEmail(authentication.getName());
            document.setUploader(uploader);
        }

        Document sauvegarde = documentService.saveDocument(document);

        // Section 19 : notifie tous les membres actifs (sauf l'uploadeur) du nouveau document
        for (Membre m : membreService.getAllMembres()) {
            if (m.isActif() && (uploader == null || !m.getId().equals(uploader.getId()))) {
                notificationService.creerNotification(
                        m,
                        "Nouveau document : " + sauvegarde.getTitre(),
                        TypeNotification.DOCUMENT,
                        "/documents"
                );
            }
        }

        return "redirect:/documents";
    }

    @GetMapping("/telecharger/{id}")
    public ResponseEntity<Resource> telecharger(@PathVariable Long id) {
        Document document = documentService.getDocumentById(id);
        Resource fichier = new FileSystemResource(DOSSIER_DOCS + document.getCheminFichier());

        String nomAffiche = document.getTitre();
        String cheminStocke = document.getCheminFichier();
        int pointIndex = cheminStocke.lastIndexOf('.');
        if (pointIndex != -1) {
            nomAffiche = nomAffiche + cheminStocke.substring(pointIndex);
        }

        ContentDisposition contentDisposition = ContentDisposition.attachment()
                .filename(nomAffiche, StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .body(fichier);
    }

    @GetMapping("/evenement/{evenementId}")
    public String documentsParEvenement(@PathVariable Long evenementId, Model model) {
        model.addAttribute("evenement", evenementService.getEvenementById(evenementId));
        model.addAttribute("documents", documentService.getDocumentsParEvenement(evenementId));
        return "documents/par-evenement";
    }

    @PostMapping("/archiver/{id}")
    public String archiver(@PathVariable Long id) {
        documentService.archiverDocument(id);
        return "redirect:/documents";
    }

    @PostMapping("/desarchiver/{id}")
    public String desarchiver(@PathVariable Long id) {
        documentService.desarchiverDocument(id);
        return "redirect:/documents";
    }
}