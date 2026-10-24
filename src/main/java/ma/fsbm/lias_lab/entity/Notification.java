package ma.fsbm.lias_lab.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String message;

    @Column(nullable = false)
    private String type; // valeurs possibles : voir TypeNotification.java

    @ManyToOne
    @JoinColumn(name = "destinataire_id", nullable = false)
    private Membre destinataire;

    @Column(nullable = false)
    private boolean lu = false;

    @Column(nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    // URL vers laquelle rediriger au clic (ex : /evenements/12), peut être nulle
    private String lien;

    public Notification() {}

    public Notification(String message, String type, Membre destinataire, String lien) {
        this.message = message;
        this.type = type;
        this.destinataire = destinataire;
        this.lien = lien;
        this.dateCreation = LocalDateTime.now();
        this.lu = false;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Membre getDestinataire() { return destinataire; }
    public void setDestinataire(Membre destinataire) { this.destinataire = destinataire; }

    public boolean isLu() { return lu; }
    public void setLu(boolean lu) { this.lu = lu; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public String getLien() { return lien; }
    public void setLien(String lien) { this.lien = lien; }
}