package ma.fsbm.lias_lab.repository;

import ma.fsbm.lias_lab.entity.Reunion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReunionRepository extends JpaRepository<Reunion, Long> {
    List<Reunion> findByArchiveFalseOrderByDateReunionDesc();
    List<Reunion> findByArchiveTrueOrderByDateReunionDesc();
}