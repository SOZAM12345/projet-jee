package ma.fsbm.lias_lab.repository;

import ma.fsbm.lias_lab.entity.DemandeMateriel;
import ma.fsbm.lias_lab.entity.StatutDemandeMateriel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DemandeMaterielRepository extends JpaRepository<DemandeMateriel, Long> {
    List<DemandeMateriel> findByStatut(StatutDemandeMateriel statut);
}