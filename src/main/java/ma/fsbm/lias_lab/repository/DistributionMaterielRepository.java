package ma.fsbm.lias_lab.repository;

import ma.fsbm.lias_lab.entity.DistributionMateriel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DistributionMaterielRepository extends JpaRepository<DistributionMateriel, Long> {

    List<DistributionMateriel> findByMaterielIdOrderByDateDistributionDesc(Long materielId);

    // Nouveau : ce qu'un membre a reçu (page « Mon matériel »)
    List<DistributionMateriel> findByMembreIdOrderByDateDistributionDesc(Long membreId);
}