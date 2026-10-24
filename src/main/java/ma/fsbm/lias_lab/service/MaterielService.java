package ma.fsbm.lias_lab.service;

import ma.fsbm.lias_lab.entity.DistributionMateriel;
import ma.fsbm.lias_lab.entity.Materiel;
import ma.fsbm.lias_lab.entity.Membre;
import ma.fsbm.lias_lab.repository.DistributionMaterielRepository;
import ma.fsbm.lias_lab.repository.MaterielRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaterielService {

    @Autowired
    private MaterielRepository materielRepository;

    @Autowired
    private DistributionMaterielRepository distributionRepository;

    @Autowired
    private MembreService membreService;

    public List<Materiel> getMaterielsActifs() {
        return materielRepository.findByArchiveFalseOrderByDateArriveeDesc();
    }

    public List<Materiel> getMaterielsArchives() {
        return materielRepository.findByArchiveTrueOrderByDateArriveeDesc();
    }

    public Materiel getMaterielById(Long id) {
        return materielRepository.findById(id).orElse(null);
    }

    // Section 14 : "Suivi des arrivages"
    public Materiel creerMateriel(Materiel materiel) {
        return materielRepository.save(materiel);
    }

    public void archiverMateriel(Long id) {
        Materiel m = materielRepository.findById(id).orElseThrow();
        m.setArchive(true);
        materielRepository.save(m);
    }

    public void desarchiverMateriel(Long id) {
        Materiel m = materielRepository.findById(id).orElseThrow();
        m.setArchive(false);
        materielRepository.save(m);
    }

    // Section 14 : "Distribution équitable" + "Historique complet"
    public void distribuer(Long materielId, Long membreId, int quantite) {
        Materiel materiel = materielRepository.findById(materielId).orElseThrow();
        Membre membre = membreService.getMembreById(membreId);

        DistributionMateriel distribution = new DistributionMateriel();
        distribution.setMateriel(materiel);
        distribution.setMembre(membre);
        distribution.setQuantite(quantite);
        distributionRepository.save(distribution);
    }

    public List<DistributionMateriel> getDistributions(Long materielId) {
        return distributionRepository.findByMaterielIdOrderByDateDistributionDesc(materielId);
    }

    // Section 14 : "Qui a reçu quoi" vu du côté du membre
    public List<DistributionMateriel> getDistributionsDuMembre(Long membreId) {
        return distributionRepository.findByMembreIdOrderByDateDistributionDesc(membreId);
    }

    public int getQuantiteDistribuee(Long materielId) {
        return getDistributions(materielId).stream()
                .mapToInt(DistributionMateriel::getQuantite)
                .sum();
    }

    // Membres qui peuvent recevoir du matériel : actifs, ni retraités ni anciens
    public List<Membre> getMembresEligibles() {
        return membreService.getAllMembres().stream()
                .filter(Membre::isActif)
                .filter(m -> m.getStatut() != null
                        && !m.getStatut().name().startsWith("RETRAIT")
                        && !m.getStatut().name().startsWith("ANCIEN"))
                .toList();
    }

    // Section 14 : "Suivi : qui n'a pas reçu" (membres éligibles uniquement)
    public List<Membre> getMembresSansDistribution(Long materielId) {
        List<Long> idsAyantRecu = getDistributions(materielId).stream()
                .map(d -> d.getMembre().getId())
                .distinct()
                .toList();

        return getMembresEligibles().stream()
                .filter(m -> !idsAyantRecu.contains(m.getId()))
                .toList();
    }
}