package ma.fsbm.lias_lab.service;

import ma.fsbm.lias_lab.entity.ActiviteConvention;
import ma.fsbm.lias_lab.entity.Convention;
import ma.fsbm.lias_lab.repository.ActiviteConventionRepository;
import ma.fsbm.lias_lab.repository.ConventionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConventionService {

    @Autowired
    private ConventionRepository conventionRepository;

    @Autowired
    private ActiviteConventionRepository activiteRepository;

    public List<Convention> getConventionsActives() {
        return conventionRepository.findByArchiveFalseOrderByDateSignatureDesc();
    }

    public List<Convention> getConventionsArchivees() {
        return conventionRepository.findByArchiveTrueOrderByDateSignatureDesc();
    }

    public Convention getConventionById(Long id) {
        return conventionRepository.findById(id).orElse(null);
    }

    public List<Convention> getConventionsParPartenaire(Long partenaireId) {
        return conventionRepository.findByPartenaireIdOrderByDateSignatureDesc(partenaireId);
    }

    public Convention creerConvention(Convention convention) {
        return conventionRepository.save(convention);
    }

    public void attacherDocument(Long conventionId, String cheminDocument) {
        Convention c = conventionRepository.findById(conventionId).orElseThrow();
        c.setCheminDocument(cheminDocument);
        conventionRepository.save(c);
    }

    public void archiverConvention(Long id) {
        Convention c = conventionRepository.findById(id).orElseThrow();
        c.setArchive(true);
        conventionRepository.save(c);
    }

    public void desarchiverConvention(Long id) {
        Convention c = conventionRepository.findById(id).orElseThrow();
        c.setArchive(false);
        conventionRepository.save(c);
    }

    // Section 16 : "Suivi des activités associées"
    public void ajouterActivite(ActiviteConvention activite) {
        activiteRepository.save(activite);
    }

    public List<ActiviteConvention> getActivites(Long conventionId) {
        return activiteRepository.findByConventionIdOrderByDateDesc(conventionId);
    }
}