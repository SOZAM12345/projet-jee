package ma.fsbm.lias_lab.controller;

import ma.fsbm.lias_lab.entity.*;
import ma.fsbm.lias_lab.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/discussions")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private MembreService membreService;

    @Autowired
    private EquipeService equipeService;

    @Autowired
    private EvenementService evenementService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public String mesConversations(Model model, Authentication authentication) {
        Membre moi = membreService.getMembreByEmail(authentication.getName());
        model.addAttribute("moi", moi);

        List<Membre> interlocuteurs = messageService.getInterlocuteurs(moi.getId());
        model.addAttribute("interlocuteurs", interlocuteurs);

        Map<Long, Long> nonLusParInterlocuteur = new HashMap<>();
        for (Membre p : interlocuteurs) {
            long compte = notificationService.compterNonLusPourLien(moi, "/discussions/avec/" + p.getId());
            nonLusParInterlocuteur.put(p.getId(), compte);
        }
        model.addAttribute("nonLusParInterlocuteur", nonLusParInterlocuteur);
        model.addAttribute("tousLesMembres", membreService.getAllMembres());

        model.addAttribute("notifsEquipeNonLues",
                notificationService.getNotificationsNonLuesParType(moi, TypeNotification.MESSAGE_EQUIPE));
        model.addAttribute("notifsEvenementNonLues",
                notificationService.getNotificationsNonLuesParType(moi, TypeNotification.MESSAGE_EVENEMENT));

        return "discussions/conversations";
    }

    @GetMapping("/avec/{membreId}")
    public String conversation(@PathVariable Long membreId, Model model, Authentication authentication) {
        Membre moi = membreService.getMembreByEmail(authentication.getName());
        Membre autre = membreService.getMembreById(membreId);

        notificationService.marquerLuesPourLien(moi, "/discussions/avec/" + membreId);
        model.addAttribute("nbMessagesNonLus", notificationService.compterMessagesNonLus(moi));

        model.addAttribute("moi", moi);
        model.addAttribute("autre", autre);
        model.addAttribute("messages", messageService.getConversation(moi.getId(), membreId));
        return "discussions/conversation-privee";
    }

    @PostMapping("/avec/{membreId}/envoyer")
    public String envoyerPrive(@PathVariable Long membreId, @RequestParam String contenu, Authentication authentication) {
        Membre moi = membreService.getMembreByEmail(authentication.getName());
        Membre autre = membreService.getMembreById(membreId);

        // La notification est déjà créée à l'intérieur de messageService.envoyerMessagePrive() —
        // ne pas en créer une deuxième ici, sinon le destinataire reçoit un doublon.
        messageService.envoyerMessagePrive(moi, autre, contenu);

        return "redirect:/discussions/avec/" + membreId;
    }

    // Section 13 : seuls les membres de l'équipe participent à sa discussion
    private void verifierMembreEquipe(Equipe equipe, Membre moi) {
        boolean estMembre = equipe.getMembres().stream()
                .anyMatch(m -> m.getId().equals(moi.getId()));
        if (!estMembre) {
            throw new AccessDeniedException("Vous n'êtes pas membre de cette équipe.");
        }
    }

    // Section 9/13 : seuls les organisateurs participent à la discussion de l'événement
    private void verifierOrganisateurEvenement(Evenement evenement, Membre moi) {
        boolean estOrganisateur = evenement.getOrganisateurs().stream()
                .anyMatch(m -> m.getId().equals(moi.getId()));
        if (!estOrganisateur) {
            throw new AccessDeniedException("Vous n'êtes pas organisateur de cet événement.");
        }
    }

    @GetMapping("/equipe/{id}")
    public String equipe(@PathVariable Long id, Model model, Authentication authentication) {
        Equipe equipe = equipeService.getEquipeById(id);
        Membre moi = membreService.getMembreByEmail(authentication.getName());
        verifierMembreEquipe(equipe, moi);

        model.addAttribute("titre", "Discussion — équipe " + equipe.getNom());
        model.addAttribute("messages", messageService.getMessagesEquipe(id));
        model.addAttribute("actionUrl", "/discussions/equipe/" + id + "/envoyer");

        notificationService.marquerLuesPourLien(moi, "/discussions/equipe/" + id);
        model.addAttribute("nbMessagesNonLus", notificationService.compterMessagesNonLus(moi));

        return "discussions/fil";
    }

    @PostMapping("/equipe/{id}/envoyer")
    public String envoyerEquipe(@PathVariable Long id, @RequestParam String contenu, Authentication authentication) {
        Membre auteur = membreService.getMembreByEmail(authentication.getName());
        Equipe equipe = equipeService.getEquipeById(id);
        verifierMembreEquipe(equipe, auteur);

        messageService.posterMessageEquipe(auteur, equipe, contenu);

        for (Membre m : equipe.getMembres()) {
            if (!m.getId().equals(auteur.getId())) {
                notificationService.creerNotification(
                        m,
                        auteur.getPrenom() + " a posté un message dans l'équipe " + equipe.getNom(),
                        TypeNotification.MESSAGE_EQUIPE,
                        "/discussions/equipe/" + id
                );
            }
        }

        return "redirect:/discussions/equipe/" + id;
    }

    @GetMapping("/evenement/{id}")
    public String evenement(@PathVariable Long id, Model model, Authentication authentication) {
        Evenement evenement = evenementService.getEvenementById(id);
        Membre moi = membreService.getMembreByEmail(authentication.getName());
        verifierOrganisateurEvenement(evenement, moi);

        model.addAttribute("titre", "Discussion — " + evenement.getTitre());
        model.addAttribute("messages", messageService.getMessagesEvenement(id));
        model.addAttribute("actionUrl", "/discussions/evenement/" + id + "/envoyer");

        notificationService.marquerLuesPourLien(moi, "/discussions/evenement/" + id);
        model.addAttribute("nbMessagesNonLus", notificationService.compterMessagesNonLus(moi));

        return "discussions/fil";
    }

    @PostMapping("/evenement/{id}/envoyer")
    public String envoyerEvenement(@PathVariable Long id, @RequestParam String contenu, Authentication authentication) {
        Membre auteur = membreService.getMembreByEmail(authentication.getName());
        Evenement evenement = evenementService.getEvenementById(id);
        verifierOrganisateurEvenement(evenement, auteur);

        messageService.posterMessageEvenement(auteur, evenement, contenu);

        for (Membre m : evenement.getOrganisateurs()) {
            if (!m.getId().equals(auteur.getId())) {
                notificationService.creerNotification(
                        m,
                        auteur.getPrenom() + " a posté un message dans la discussion de " + evenement.getTitre(),
                        TypeNotification.MESSAGE_EVENEMENT,
                        "/discussions/evenement/" + id
                );
            }
        }

        return "redirect:/discussions/evenement/" + id;
    }
}