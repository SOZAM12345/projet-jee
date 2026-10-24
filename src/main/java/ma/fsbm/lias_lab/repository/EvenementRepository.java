package ma.fsbm.lias_lab.repository;

import ma.fsbm.lias_lab.entity.Evenement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvenementRepository extends JpaRepository<Evenement, Long> {

    // Section 9 : regrouper toutes les éditions d'un même événement récurrent
    List<Evenement> findByTitreIgnoreCaseOrderByEditionDesc(String titre);

    List<Evenement> findByArchiveFalseOrderByDateDebutAsc();

    List<Evenement> findByArchiveTrueOrderByDateDebutDesc();
}