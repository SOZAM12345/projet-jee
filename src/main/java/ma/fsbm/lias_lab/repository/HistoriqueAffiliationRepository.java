package ma.fsbm.lias_lab.repository;

import ma.fsbm.lias_lab.entity.HistoriqueAffiliation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoriqueAffiliationRepository extends JpaRepository<HistoriqueAffiliation, Long> {

    List<HistoriqueAffiliation> findByMembreIdOrderByDateDebutDesc(Long membreId);
}