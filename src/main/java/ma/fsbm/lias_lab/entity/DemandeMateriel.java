package ma.fsbm.lias_lab.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// Section 15 : "Soumission des demandes" + "Validation par direction" + "Historique"
@Entity
@Table(name = "demande_materiel")
public class DemandeMateriel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "demandeur_id", nullable = false)
    private Membre demandeur;

    @Column(nullable = false)
    private String designation;

    private int quantiteSouhaitee;

    @Column(columnDefinition = "TEXT")
    private String motif;

    @Enumerated(EnumType.STRING)
    private StatutDemandeMateriel statut = StatutDemandeMateriel.EN_ATTENTE;

    private LocalDateTime dateSoumission = LocalDateTime.now();

    private LocalDateTime dateDecision;

    @ManyToOne
    @JoinColumn(name = "validateur_id")
    private Membre validateur;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Membre getDemandeur() { return demandeur; }
    public void setDemandeur(Membre demandeur) { this.demandeur = demandeur; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public int getQuantiteSouhaitee() { return quantiteSouhaitee; }
    public void setQuantiteSouhaitee(int quantiteSouhaitee) { this.quantiteSouhaitee = quantiteSouhaitee; }

    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }

    public StatutDemandeMateriel getStatut() { return statut; }
    public void setStatut(StatutDemandeMateriel statut) { this.statut = statut; }

    public LocalDateTime getDateSoumission() { return dateSoumission; }
    public void setDateSoumission(LocalDateTime dateSoumission) { this.dateSoumission = dateSoumission; }

    public LocalDateTime getDateDecision() { return dateDecision; }
    public void setDateDecision(LocalDateTime dateDecision) { this.dateDecision = dateDecision; }

    public Membre getValidateur() { return validateur; }
    public void setValidateur(Membre validateur) { this.validateur = validateur; }
}