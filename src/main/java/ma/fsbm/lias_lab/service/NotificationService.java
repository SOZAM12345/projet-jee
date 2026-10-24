package ma.fsbm.lias_lab.service;

import ma.fsbm.lias_lab.entity.Membre;
import ma.fsbm.lias_lab.entity.Notification;
import ma.fsbm.lias_lab.entity.TypeNotification;
import ma.fsbm.lias_lab.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public void creerNotification(Membre destinataire, String message, String type, String lien) {
        if (destinataire == null) return;
        Notification n = new Notification(message, type, destinataire, lien);
        notificationRepository.save(n);
    }

    public void creerNotificationPourPlusieurs(List<Membre> destinataires, String message, String type, String lien) {
        if (destinataires == null) return;
        for (Membre m : destinataires) {
            creerNotification(m, message, type, lien);
        }
    }

    public List<Notification> getNotifications(Membre membre) {
        return notificationRepository.findByDestinataireOrderByDateCreationDesc(membre);
    }

    public List<Notification> getNotificationsNonLues(Membre membre) {
        return notificationRepository.findByDestinataireAndLuFalseOrderByDateCreationDesc(membre);
    }

    public long compterNonLues(Membre membre) {
        return notificationRepository.countByDestinataireAndLuFalse(membre);
    }

    public long compterNonLusParType(Membre membre, String type) {
        return notificationRepository.countByDestinataireAndTypeAndLuFalse(membre, type);
    }

    // Badge "Discussions" de la navbar : somme des 3 types de messages (privé, équipe, événement)
    public long compterMessagesNonLus(Membre membre) {
        return compterNonLusParType(membre, TypeNotification.MESSAGE)
                + compterNonLusParType(membre, TypeNotification.MESSAGE_EQUIPE)
                + compterNonLusParType(membre, TypeNotification.MESSAGE_EVENEMENT);
    }

    // Notifications non lues d'un type précis, pour les lister avec leur lien
    public List<Notification> getNotificationsNonLuesParType(Membre membre, String type) {
        return getNotificationsNonLues(membre).stream()
                .filter(n -> type.equals(n.getType()))
                .toList();
    }

    public void marquerCommeLue(Long id) {
        notificationRepository.findById(id).ifPresent(n -> {
            n.setLu(true);
            notificationRepository.save(n);
        });
    }

    public void marquerToutesCommeLues(Membre membre) {
        List<Notification> nonLues = getNotificationsNonLues(membre);
        nonLues.forEach(n -> n.setLu(true));
        notificationRepository.saveAll(nonLues);
    }

    public void marquerLuesParType(Membre membre, String type) {
        List<Notification> notifs = getNotificationsNonLues(membre).stream()
                .filter(n -> type.equals(n.getType()))
                .toList();
        notifs.forEach(n -> n.setLu(true));
        notificationRepository.saveAll(notifs);
    }

    public void marquerLuesPourLien(Membre destinataire, String lien) {
        List<Notification> notifs = notificationRepository.findByDestinataireAndLienAndLuFalse(destinataire, lien);
        notifs.forEach(n -> n.setLu(true));
        notificationRepository.saveAll(notifs);
    }

    public long compterNonLusPourLien(Membre destinataire, String lien) {
        return notificationRepository.countByDestinataireAndLienAndLuFalse(destinataire, lien);
    }

    public void supprimerParLien(String lien) {
        if (lien == null || lien.isBlank()) return;
        notificationRepository.deleteByLien(lien);
    }
}