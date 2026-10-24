package ma.fsbm.lias_lab.service;

import ma.fsbm.lias_lab.entity.Publication;
import ma.fsbm.lias_lab.repository.PublicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PublicationService {

    @Autowired
    private PublicationRepository publicationRepository;

    public List<Publication> getPublicationsActives() {
        return publicationRepository.findByArchiveFalseOrderByAnneeDesc();
    }

    public List<Publication> getPublicationsArchivees() {
        return publicationRepository.findByArchiveTrueOrderByAnneeDesc();
    }

    public Publication getPublicationById(Long id) {
        return publicationRepository.findById(id).orElse(null);
    }

    public Publication savePublication(Publication publication) {
        String lien = publication.getLien();
        if (lien != null && !lien.isBlank()
                && !lien.startsWith("http://")
                && !lien.startsWith("https://")) {
            publication.setLien("https://" + lien);
        }
        return publicationRepository.save(publication);
    }

    // Section 12 : classement combinable par auteur, année, équipe
    public List<Publication> rechercher(Long auteurId, Integer annee, Long equipeId) {
        return publicationRepository.rechercher(auteurId, annee, equipeId);
    }

    // Section 3 : affichage des publications sur le profil d'un membre
    public List<Publication> getPublicationsParAuteur(Long membreId) {
        return publicationRepository.rechercher(membreId, null, null);
    }

    // Section 21 : remplace l'ancien DELETE physique
    public void archiverPublication(Long id) {
        Publication p = publicationRepository.findById(id).orElseThrow();
        p.setArchive(true);
        publicationRepository.save(p);
    }

    public void desarchiverPublication(Long id) {
        Publication p = publicationRepository.findById(id).orElseThrow();
        p.setArchive(false);
        publicationRepository.save(p);
    }
}