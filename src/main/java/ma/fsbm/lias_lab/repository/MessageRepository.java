package ma.fsbm.lias_lab.repository;

import ma.fsbm.lias_lab.entity.Membre;
import ma.fsbm.lias_lab.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByEquipeIdOrderByDateEnvoiAsc(Long equipeId);

    List<Message> findByEvenementIdOrderByDateEnvoiAsc(Long evenementId);

    // Section 13 : conversation privée entre deux membres précis, dans les deux sens
    @Query("SELECT m FROM Message m WHERE m.destinataire IS NOT NULL AND " +
           "((m.auteur.id = :membre1 AND m.destinataire.id = :membre2) OR " +
           "(m.auteur.id = :membre2 AND m.destinataire.id = :membre1)) " +
           "ORDER BY m.dateEnvoi ASC")
    List<Message> findConversation(@Param("membre1") Long membre1, @Param("membre2") Long membre2);

    // Section 13 : messages privés envoyés par ce membre, pour en extraire les destinataires
    @Query("SELECT DISTINCT m.destinataire FROM Message m WHERE m.destinataire IS NOT NULL AND m.auteur.id = :membreId")
    List<Membre> findDestinatairesEnvoyes(@Param("membreId") Long membreId);

    // Section 13 : messages privés reçus par ce membre, pour en extraire les auteurs
    @Query("SELECT DISTINCT m.auteur FROM Message m WHERE m.destinataire IS NOT NULL AND m.destinataire.id = :membreId")
    List<Membre> findAuteursRecus(@Param("membreId") Long membreId);
}