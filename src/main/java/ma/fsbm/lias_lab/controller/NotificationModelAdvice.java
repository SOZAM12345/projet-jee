package ma.fsbm.lias_lab.controller;

import ma.fsbm.lias_lab.entity.Membre;
import ma.fsbm.lias_lab.entity.TypeNotification;
import ma.fsbm.lias_lab.service.MembreService;
import ma.fsbm.lias_lab.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class NotificationModelAdvice {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private MembreService membreService;

    private Membre membreConnecte(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        return membreService.getMembreByEmail(authentication.getName());
    }

    @ModelAttribute("nbNotificationsNonLues")
    public long nbNotificationsNonLues(Authentication authentication) {
        Membre membre = membreConnecte(authentication);
        if (membre == null) return 0;
        return notificationService.compterNonLues(membre);
    }

    // Section 19 : badge par module, affiché à côté du lien correspondant dans la navbar
    @ModelAttribute("nbNotifEvenements")
    public long nbNotifEvenements(Authentication authentication) {
        Membre membre = membreConnecte(authentication);
        if (membre == null) return 0;
        return notificationService.compterNonLusParType(membre, TypeNotification.EVENEMENT);
    }

    @ModelAttribute("nbNotifDocuments")
    public long nbNotifDocuments(Authentication authentication) {
        Membre membre = membreConnecte(authentication);
        if (membre == null) return 0;
        return notificationService.compterNonLusParType(membre, TypeNotification.DOCUMENT);
    }

    @ModelAttribute("nbNotifValidationDemande")
    public long nbNotifValidationDemande(Authentication authentication) {
        Membre membre = membreConnecte(authentication);
        if (membre == null) return 0;
        return notificationService.compterNonLusParType(membre, TypeNotification.VALIDATION_DEMANDE);
    }

    @ModelAttribute("nbNotifMaterielRecu")
    public long nbNotifMaterielRecu(Authentication authentication) {
        Membre membre = membreConnecte(authentication);
        if (membre == null) return 0;
        return notificationService.compterNonLusParType(membre, TypeNotification.MATERIEL_RECU);
    }
}