package ma.fsbm.lias_lab.repository;

import ma.fsbm.lias_lab.entity.Partenaire;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PartenaireRepository extends JpaRepository<Partenaire, Long> {
    List<Partenaire> findByArchiveFalseOrderByNomAsc();
    List<Partenaire> findByArchiveTrueOrderByNomAsc();
}