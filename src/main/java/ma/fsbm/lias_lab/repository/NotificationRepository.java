package ma.fsbm.lias_lab.repository;

import ma.fsbm.lias_lab.entity.Membre;
import ma.fsbm.lias_lab.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByDestinataireOrderByDateCreationDesc(Membre destinataire);

    List<Notification> findByDestinataireAndLuFalseOrderByDateCreationDesc(Membre destinataire);

    long countByDestinataireAndLuFalse(Membre destinataire);

    long countByDestinataireAndTypeAndLuFalse(Membre destinataire, String type);

    List<Notification> findByDestinataireAndLienAndLuFalse(Membre destinataire, String lien);

    // Nouveau : compteur de messages non lus venant d'une personne précise
    long countByDestinataireAndLienAndLuFalse(Membre destinataire, String lien);

    @Transactional
    void deleteByLien(String lien);
}