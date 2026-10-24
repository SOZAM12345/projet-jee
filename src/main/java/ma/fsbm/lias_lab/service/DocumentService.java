package ma.fsbm.lias_lab.service;

import ma.fsbm.lias_lab.entity.Document;
import ma.fsbm.lias_lab.entity.Membre;
import ma.fsbm.lias_lab.entity.StatutMembre;
import ma.fsbm.lias_lab.entity.TypeDocument;
import ma.fsbm.lias_lab.entity.TypeNotification;
import ma.fsbm.lias_lab.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private MembreService membreService;

    @Autowired
    private NotificationService notificationService;

    public List<Document> getDocumentsActifs() {
        return documentRepository.findByArchiveFalseOrderByDateUploadDesc();
    }

    public List<Document> getDocumentsArchives() {
        return documentRepository.findByArchiveTrueOrderByDateUploadDesc();
    }

    public Document getDocumentById(Long id) {
        return documentRepository.findById(id).orElse(null);
    }

    public List<Document> getDocumentsParEvenement(Long evenementId) {
        return documentRepository.findByEvenementIdOrderByDateUploadDesc(evenementId);
    }

    public List<Document> getDocumentsParType(TypeDocument type) {
        return documentRepository.findByTypeOrderByDateUploadDesc(type);
    }

    public List<Document> rechercher(String motCle) {
        if (motCle == null || motCle.isBlank()) {
            return getDocumentsActifs();
        }
        return documentRepository.findByTitreContainingIgnoreCaseOrderByDateUploadDesc(motCle);
    }

    public Document saveDocument(Document document) {
        boolean estNouveau = (document.getId() == null);
        Document enregistre = documentRepository.save(document);

        // Section 19 : notifier les membres ayant accès à /documents/**
        // Cahier §2.2.C : Doctorant exclu ("aucun accès aux modules internes")
        // Cahier §2.2.B : Associé inclus (le cahier dit seulement "Consultation", sans exclusion)
        if (estNouveau) {
            List<Membre> membresAvecAcces = membreService.getAllMembres().stream()
                    .filter(m -> m.getStatut() != StatutMembre.DOCTORANT)
                    .toList();

            notificationService.creerNotificationPourPlusieurs(
                    membresAvecAcces,
                    "Nouveau document : " + enregistre.getTitre(),
                    TypeNotification.DOCUMENT,
                    "/documents/telecharger/" + enregistre.getId()
            );
        }

        return enregistre;
    }

    public void archiverDocument(Long id) {
        Document d = documentRepository.findById(id).orElseThrow();
        d.setArchive(true);
        documentRepository.save(d);
        notificationService.supprimerParLien("/documents/telecharger/" + id);
    }

    public void desarchiverDocument(Long id) {
        Document d = documentRepository.findById(id).orElseThrow();
        d.setArchive(false);
        documentRepository.save(d);
    }
}