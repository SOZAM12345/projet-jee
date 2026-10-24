package ma.fsbm.lias_lab.repository;

import ma.fsbm.lias_lab.entity.Publication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PublicationRepository extends JpaRepository<Publication, Long> {

    List<Publication> findByArchiveFalseOrderByAnneeDesc();
    List<Publication> findByArchiveTrueOrderByAnneeDesc();

    // Section 12 : "Classement par auteur, année, équipe" — combinables, pas exclusifs
    @Query("SELECT DISTINCT p FROM Publication p LEFT JOIN p.auteurs a " +
           "WHERE p.archive = false " +
           "AND (:auteurId IS NULL OR a.id = :auteurId) " +
           "AND (:annee IS NULL OR p.annee = :annee) " +
           "AND (:equipeId IS NULL OR p.equipe.id = :equipeId) " +
           "ORDER BY p.annee DESC")
    List<Publication> rechercher(@Param("auteurId") Long auteurId,
                                  @Param("annee") Integer annee,
                                  @Param("equipeId") Long equipeId);
}