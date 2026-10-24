package ma.fsbm.lias_lab.config;

import ma.fsbm.lias_lab.entity.Membre;
import ma.fsbm.lias_lab.service.MembreService;
import ma.fsbm.lias_lab.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class BadgeMessagesAdvice {

    @Autowired
    private MembreService membreService;

    @Autowired
    private NotificationService notificationService;

    @ModelAttribute("nbMessagesNonLus")
    public long nbMessagesNonLus(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return 0;
        }
        Membre membre = membreService.getMembreByEmail(authentication.getName());
        if (membre == null) return 0;
        return notificationService.compterMessagesNonLus(membre);
    }
}