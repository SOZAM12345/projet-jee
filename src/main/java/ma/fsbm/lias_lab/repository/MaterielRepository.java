package ma.fsbm.lias_lab.repository;

import ma.fsbm.lias_lab.entity.Materiel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaterielRepository extends JpaRepository<Materiel, Long> {
    List<Materiel> findByArchiveFalseOrderByDateArriveeDesc();
    List<Materiel> findByArchiveTrueOrderByDateArriveeDesc();
}