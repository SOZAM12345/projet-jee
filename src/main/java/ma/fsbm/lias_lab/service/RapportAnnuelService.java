package ma.fsbm.lias_lab.service;

import ma.fsbm.lias_lab.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RapportAnnuelService {

    @Autowired
    private EvenementService evenementService;

    @Autowired
    private PublicationService publicationService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private ReunionService reunionService;

    @Autowired
    private ConventionService conventionService;

    @Autowired
    private DemandeMaterielService demandeMaterielService;

    // Section 11 : "Génération annuelle automatique, basé sur événements, publications, activités"
    public RapportAnnuel genererRapport(int annee) {
        RapportAnnuel rapport = new RapportAnnuel();
        rapport.setAnnee(annee);

        List<Evenement> tousEvenements = evenementService.getEvenementsActifs();
        tousEvenements.addAll(evenementService.getEvenementsArchives());
        rapport.setEvenements(tousEvenements.stream()
                .filter(e -> e.getDateDebut() != null && e.getDateDebut().getYear() == annee)
                .toList());

        List<Publication> toutesPublications = publicationService.getPublicationsActives();
        toutesPublications.addAll(publicationService.getPublicationsArchivees());
        rapport.setPublications(toutesPublications.stream()
                .filter(p -> p.getAnnee() != null && p.getAnnee() == annee)
                .toList());

        List<Document> tousDocuments = documentService.getDocumentsActifs();
        tousDocuments.addAll(documentService.getDocumentsArchives());
        rapport.setDocuments(tousDocuments.stream()
                .filter(d -> d.getDateUpload() != null && d.getDateUpload().getYear() == annee)
                .toList());

        List<Reunion> toutesReunions = reunionService.getReunionsActives();
        toutesReunions.addAll(reunionService.getReunionsArchivees());
        rapport.setReunions(toutesReunions.stream()
                .filter(r -> r.getDateReunion() != null && r.getDateReunion().getYear() == annee)
                .toList());

        List<Convention> toutesConventions = conventionService.getConventionsActives();
        toutesConventions.addAll(conventionService.getConventionsArchivees());
        rapport.setConventions(toutesConventions.stream()
                .filter(c -> c.getDateSignature() != null && c.getDateSignature().getYear() == annee)
                .toList());

        List<DemandeMateriel> toutesDemandes = demandeMaterielService.getAllDemandes();
        rapport.setDemandesMateriel(toutesDemandes.stream()
                .filter(d -> d.getDateSoumission() != null && d.getDateSoumission().getYear() == annee)
                .toList());

        return rapport;
    }
}