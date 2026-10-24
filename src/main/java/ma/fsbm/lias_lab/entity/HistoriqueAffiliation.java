package ma.fsbm.lias_lab.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class HistoriqueAffiliation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "membre_id", nullable = false)
    private Membre membre;

    private LocalDate dateDebut;
    private LocalDate dateFin; // null = période en cours (membre toujours affilié)

    private String motifOuverture; // ex: "Adhésion initiale", "Réintégration"
    private String motifCloture;   // ex: "Désactivation - ancien membre", "Mise en retraite"

    public HistoriqueAffiliation() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Membre getMembre() { return membre; }
    public void setMembre(Membre membre) { this.membre = membre; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

    public String getMotifOuverture() { return motifOuverture; }
    public void setMotifOuverture(String motifOuverture) { this.motifOuverture = motifOuverture; }

    public String getMotifCloture() { return motifCloture; }
    public void setMotifCloture(String motifCloture) { this.motifCloture = motifCloture; }
}