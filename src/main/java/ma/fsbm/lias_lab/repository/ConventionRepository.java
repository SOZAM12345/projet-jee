package ma.fsbm.lias_lab.repository;

import ma.fsbm.lias_lab.entity.Convention;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConventionRepository extends JpaRepository<Convention, Long> {
    List<Convention> findByArchiveFalseOrderByDateSignatureDesc();
    List<Convention> findByArchiveTrueOrderByDateSignatureDesc();
    List<Convention> findByPartenaireIdOrderByDateSignatureDesc(Long partenaireId);
}