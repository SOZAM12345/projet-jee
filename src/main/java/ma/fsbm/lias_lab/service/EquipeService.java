package ma.fsbm.lias_lab.service;

import ma.fsbm.lias_lab.entity.Equipe;
import ma.fsbm.lias_lab.repository.EquipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EquipeService {

    @Autowired
    private EquipeRepository equipeRepository;

    public List<Equipe> getAllEquipes() {
        return equipeRepository.findAll();
    }

    public List<Equipe> getEquipesActives() {
        return equipeRepository.findByArchiveFalseOrderByNomAsc();
    }

    public List<Equipe> getEquipesArchivees() {
        return equipeRepository.findByArchiveTrueOrderByNomAsc();
    }

    public Equipe getEquipeById(Long id) {
        return equipeRepository.findById(id).orElse(null);
    }

    public Equipe saveEquipe(Equipe equipe) {
        return equipeRepository.save(equipe);
    }

    // Section 21 : remplace l'ancien DELETE physique — les membres et mandats
    // liés restent intacts, l'équipe disparaît juste des listes actives
    public void archiverEquipe(Long id) {
        Equipe e = equipeRepository.findById(id).orElseThrow();
        e.setArchive(true);
        equipeRepository.save(e);
    }

    public void desarchiverEquipe(Long id) {
        Equipe e = equipeRepository.findById(id).orElseThrow();
        e.setArchive(false);
        equipeRepository.save(e);
    }
}