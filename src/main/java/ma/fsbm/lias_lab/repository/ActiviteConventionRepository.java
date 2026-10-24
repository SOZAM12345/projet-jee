package ma.fsbm.lias_lab.repository;

import ma.fsbm.lias_lab.entity.ActiviteConvention;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActiviteConventionRepository extends JpaRepository<ActiviteConvention, Long> {
    List<ActiviteConvention> findByConventionIdOrderByDateDesc(Long conventionId);
}