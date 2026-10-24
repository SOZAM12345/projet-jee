package ma.fsbm.lias_lab.repository;

import ma.fsbm.lias_lab.entity.HistoriqueModificationMembre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoriqueModificationMembreRepository extends JpaRepository<HistoriqueModificationMembre, Long> {
    List<HistoriqueModificationMembre> findByMembreIdOrderByDateModificationDesc(Long membreId);
}