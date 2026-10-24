package ma.fsbm.lias_lab.service;

import ma.fsbm.lias_lab.entity.Reunion;
import ma.fsbm.lias_lab.repository.ReunionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReunionService {

    @Autowired
    private ReunionRepository reunionRepository;

    public List<Reunion> getReunionsActives() {
        return reunionRepository.findByArchiveFalseOrderByDateReunionDesc();
    }

    public List<Reunion> getReunionsArchivees() {
        return reunionRepository.findByArchiveTrueOrderByDateReunionDesc();
    }

    public Reunion getReunionById(Long id) {
        return reunionRepository.findById(id).orElse(null);
    }

    // Section 17 : "Création des réunions" + "Ordre du jour"
    public Reunion creerReunion(Reunion reunion) {
        return reunionRepository.save(reunion);
    }

    // Section 17 : "Upload des PV" — attaché après coup à une réunion existante
    public void attacherPv(Long reunionId, String cheminPv) {
        Reunion r = reunionRepository.findById(reunionId).orElseThrow();
        r.setCheminPv(cheminPv);
        reunionRepository.save(r);
    }

    // Section 17 + 21 : "Archivage" sans suppression physique
    public void archiverReunion(Long id) {
        Reunion r = reunionRepository.findById(id).orElseThrow();
        r.setArchive(true);
        reunionRepository.save(r);
    }

    public void desarchiverReunion(Long id) {
        Reunion r = reunionRepository.findById(id).orElseThrow();
        r.setArchive(false);
        reunionRepository.save(r);
    }
}