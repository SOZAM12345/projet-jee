package ma.fsbm.lias_lab.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "mandat")
public class Mandat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeRole typeRole;

    @ManyToOne
    @JoinColumn(name = "membre_id", nullable = false)
    private Membre membre;

    @ManyToOne
    @JoinColumn(name = "equipe_id")
    private Equipe equipe; // optionnel, utile seulement pour CHEF_EQUIPE / MEMBRE_EQUIPE

    private LocalDate dateDebut;
    private LocalDate dateFin; // null = mandat en cours

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TypeRole getTypeRole() { return typeRole; }
    public void setTypeRole(TypeRole typeRole) { this.typeRole = typeRole; }

    public Membre getMembre() { return membre; }
    public void setMembre(Membre membre) { this.membre = membre; }

    public Equipe getEquipe() { return equipe; }
    public void setEquipe(Equipe equipe) { this.equipe = equipe; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
}