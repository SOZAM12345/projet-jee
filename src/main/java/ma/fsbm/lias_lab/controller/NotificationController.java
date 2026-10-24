package ma.fsbm.lias_lab.controller;

import ma.fsbm.lias_lab.entity.Membre;
import ma.fsbm.lias_lab.entity.Notification;
import ma.fsbm.lias_lab.service.MembreService;
import ma.fsbm.lias_lab.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private MembreService membreService;

    // Section 19 : liste complète des notifications du membre connecté
    @GetMapping("/notifications")
    public String liste(Model model, Authentication authentication) {
        Membre membre = membreService.getMembreByEmail(authentication.getName());
        List<Notification> notifications = notificationService.getNotifications(membre);
        model.addAttribute("notifications", notifications);
        return "notifications/liste";
    }

    // Marquer une notification comme lue, puis rediriger vers sa cible (ou la liste)
    @PostMapping("/notifications/lire/{id}")
    public String lire(@PathVariable Long id) {
        notificationService.marquerCommeLue(id);
        return "redirect:/notifications";
    }

    @PostMapping("/notifications/lire-tout")
    public String lireTout(Authentication authentication) {
        Membre membre = membreService.getMembreByEmail(authentication.getName());
        notificationService.marquerToutesCommeLues(membre);
        return "redirect:/notifications";
    }
}