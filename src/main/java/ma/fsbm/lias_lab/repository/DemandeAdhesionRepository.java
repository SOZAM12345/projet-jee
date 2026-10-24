package ma.fsbm.lias_lab.repository;

import ma.fsbm.lias_lab.entity.DemandeAdhesion;
import ma.fsbm.lias_lab.entity.StatutDemande;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DemandeAdhesionRepository extends JpaRepository<DemandeAdhesion, Long> {
    List<DemandeAdhesion> findByStatut(StatutDemande statut);
}