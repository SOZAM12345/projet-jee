package ma.fsbm.lias_lab.service;

import ma.fsbm.lias_lab.entity.Partenaire;
import ma.fsbm.lias_lab.repository.PartenaireRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PartenaireService {

    @Autowired
    private PartenaireRepository partenaireRepository;

    public List<Partenaire> getPartenairesActifs() {
        return partenaireRepository.findByArchiveFalseOrderByNomAsc();
    }

    public List<Partenaire> getPartenairesArchives() {
        return partenaireRepository.findByArchiveTrueOrderByNomAsc();
    }

    public Partenaire getPartenaireById(Long id) {
        return partenaireRepository.findById(id).orElse(null);
    }

    public Partenaire creerPartenaire(Partenaire partenaire) {
        return partenaireRepository.save(partenaire);
    }

    public void archiverPartenaire(Long id) {
        Partenaire p = partenaireRepository.findById(id).orElseThrow();
        p.setArchive(true);
        partenaireRepository.save(p);
    }

    public void desarchiverPartenaire(Long id) {
        Partenaire p = partenaireRepository.findById(id).orElseThrow();
        p.setArchive(false);
        partenaireRepository.save(p);
    }
}