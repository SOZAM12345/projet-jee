package ma.fsbm.lias_lab.service;

import ma.fsbm.lias_lab.entity.*;
import ma.fsbm.lias_lab.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private NotificationService notificationService;

    public List<Message> getMessagesEquipe(Long equipeId) {
        return messageRepository.findByEquipeIdOrderByDateEnvoiAsc(equipeId);
    }

    public List<Message> getMessagesEvenement(Long evenementId) {
        return messageRepository.findByEvenementIdOrderByDateEnvoiAsc(evenementId);
    }

    public void posterMessageEquipe(Membre auteur, Equipe equipe, String contenu) {
        messageRepository.save(new Message(contenu, auteur, equipe, null));
    }

    public void posterMessageEvenement(Membre auteur, Evenement evenement, String contenu) {
        messageRepository.save(new Message(contenu, auteur, null, evenement));
    }

    // Section 13 : "Discussions entre membres" — messagerie privée
    public List<Message> getConversation(Long membre1, Long membre2) {
        return messageRepository.findConversation(membre1, membre2);
    }

    public List<Membre> getInterlocuteurs(Long membreId) {
        List<Membre> resultat = new ArrayList<>();
        resultat.addAll(messageRepository.findDestinatairesEnvoyes(membreId));
        for (Membre m : messageRepository.findAuteursRecus(membreId)) {
            if (!resultat.contains(m)) {
                resultat.add(m);
            }
        }
        return resultat;
    }

    // Section 13 + 19 : l'envoi d'un message privé déclenche une notification pour le destinataire
    public void envoyerMessagePrive(Membre auteur, Membre destinataire, String contenu) {
        messageRepository.save(new Message(contenu, auteur, destinataire));

        String texteNotif = auteur.getPrenom() + " " + auteur.getNom() + " vous a envoyé un message.";
        String lien = "/discussions/avec/" + auteur.getId();
        notificationService.creerNotification(destinataire, texteNotif, "MESSAGE", lien);
    }
}