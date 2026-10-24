package ma.fsbm.lias_lab.repository;

import ma.fsbm.lias_lab.entity.Equipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipeRepository extends JpaRepository<Equipe, Long> {
    List<Equipe> findByArchiveFalseOrderByNomAsc();
    List<Equipe> findByArchiveTrueOrderByNomAsc();
}