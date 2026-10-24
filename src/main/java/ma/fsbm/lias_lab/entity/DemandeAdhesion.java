package ma.fsbm.lias_lab.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "demande_adhesion")
public class DemandeAdhesion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String motivation;

    // Chemin vers le fichier CV uploadé (section 6 : "Informations complètes (CV, motivation…)")
    private String cheminCv;

    @Enumerated(EnumType.STRING)
    private StatutDemande statut = StatutDemande.EN_ATTENTE;

    private LocalDateTime dateSoumission = LocalDateTime.now();

    // Traçabilité de la décision (section 21 : "toute action importante est traçable")
    private LocalDateTime dateDecision;

    // Le directeur en mandat qui a accepté/refusé (section 6 : "Gestion : uniquement par le directeur en mandat")
    @ManyToOne
    @JoinColumn(name = "validateur_id")
    private Membre validateur;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMotivation() { return motivation; }
    public void setMotivation(String motivation) { this.motivation = motivation; }

    public String getCheminCv() { return cheminCv; }
    public void setCheminCv(String cheminCv) { this.cheminCv = cheminCv; }

    public StatutDemande getStatut() { return statut; }
    public void setStatut(StatutDemande statut) { this.statut = statut; }

    public LocalDateTime getDateSoumission() { return dateSoumission; }
    public void setDateSoumission(LocalDateTime dateSoumission) { this.dateSoumission = dateSoumission; }

    public LocalDateTime getDateDecision() { return dateDecision; }
    public void setDateDecision(LocalDateTime dateDecision) { this.dateDecision = dateDecision; }

    public Membre getValidateur() { return validateur; }
    public void setValidateur(Membre validateur) { this.validateur = validateur; }
}