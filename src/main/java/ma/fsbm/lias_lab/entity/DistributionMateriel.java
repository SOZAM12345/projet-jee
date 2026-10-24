package ma.fsbm.lias_lab.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

// Section 14 : "Suivi : qui a reçu quoi" + "Historique complet"
@Entity
@Table(name = "distribution_materiel")
public class DistributionMateriel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "materiel_id", nullable = false)
    private Materiel materiel;

    @ManyToOne
    @JoinColumn(name = "membre_id", nullable = false)
    private Membre membre;

    @Column(nullable = false)
    private int quantite;

    private LocalDate dateDistribution = LocalDate.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Materiel getMateriel() { return materiel; }
    public void setMateriel(Materiel materiel) { this.materiel = materiel; }

    public Membre getMembre() { return membre; }
    public void setMembre(Membre membre) { this.membre = membre; }

    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }

    public LocalDate getDateDistribution() { return dateDistribution; }
    public void setDateDistribution(LocalDate dateDistribution) { this.dateDistribution = dateDistribution; }
}