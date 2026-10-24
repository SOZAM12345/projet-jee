package ma.fsbm.lias_lab.entity;

// Section 19 : catégories de notifications (constantes, pas un enum)
public class TypeNotification {

    public static final String EVENEMENT = "EVENEMENT";
    public static final String VALIDATION_DEMANDE = "VALIDATION_DEMANDE";
    public static final String DOCUMENT = "DOCUMENT";
    public static final String MATERIEL_RECU = "MATERIEL_RECU";

    // Trois types séparés pour distinguer d'où vient un message
    public static final String MESSAGE = "MESSAGE";
    public static final String MESSAGE_EQUIPE = "MESSAGE_EQUIPE";
    public static final String MESSAGE_EVENEMENT = "MESSAGE_EVENEMENT";

    private TypeNotification() {
        // classe utilitaire, non instanciable
    }
}