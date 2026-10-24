package ma.fsbm.lias_lab.repository;

import ma.fsbm.lias_lab.entity.Membre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MembreRepository extends JpaRepository<Membre, Long> {

    Optional<Membre> findByEmail(String email);

    // Utilisée par l'authentification : charge le membre ET ses mandats
    // en une seule requête (JOIN FETCH), pour éviter la LazyInitializationException
    @Query("SELECT m FROM Membre m LEFT JOIN FETCH m.mandats WHERE m.email = :email")
    Optional<Membre> findByEmailWithMandats(@Param("email") String email);
}