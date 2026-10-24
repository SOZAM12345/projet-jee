package ma.fsbm.lias_lab.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2000)
    private String contenu;

    @ManyToOne
    @JoinColumn(name = "auteur_id", nullable = false)
    private Membre auteur;

    @Column(nullable = false)
    private LocalDateTime dateEnvoi = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "equipe_id")
    private Equipe equipe;

    @ManyToOne
    @JoinColumn(name = "evenement_id")
    private Evenement evenement;

    // Section 13 : "Discussions entre membres" — messagerie privée à deux
    @ManyToOne
    @JoinColumn(name = "destinataire_id")
    private Membre destinataire;

    public Message() {}

    public Message(String contenu, Membre auteur, Equipe equipe, Evenement evenement) {
        this.contenu = contenu;
        this.auteur = auteur;
        this.equipe = equipe;
        this.evenement = evenement;
        this.dateEnvoi = LocalDateTime.now();
    }

    public Message(String contenu, Membre auteur, Membre destinataire) {
        this.contenu = contenu;
        this.auteur = auteur;
        this.destinataire = destinataire;
        this.dateEnvoi = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public Membre getAuteur() { return auteur; }
    public void setAuteur(Membre auteur) { this.auteur = auteur; }

    public LocalDateTime getDateEnvoi() { return dateEnvoi; }
    public void setDateEnvoi(LocalDateTime dateEnvoi) { this.dateEnvoi = dateEnvoi; }

    public Equipe getEquipe() { return equipe; }
    public void setEquipe(Equipe equipe) { this.equipe = equipe; }

    public Evenement getEvenement() { return evenement; }
    public void setEvenement(Evenement evenement) { this.evenement = evenement; }

    public Membre getDestinataire() { return destinataire; }
    public void setDestinataire(Membre destinataire) { this.destinataire = destinataire; }
}