package ma.fsbm.lias_lab.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class HistoriqueModificationMembre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "membre_id", nullable = false)
    private Membre membre;

    private String champModifie;

    @Column(columnDefinition = "TEXT")
    private String ancienneValeur;

    @Column(columnDefinition = "TEXT")
    private String nouvelleValeur;

    private LocalDateTime dateModification = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Membre getMembre() { return membre; }
    public void setMembre(Membre membre) { this.membre = membre; }

    public String getChampModifie() { return champModifie; }
    public void setChampModifie(String champModifie) { this.champModifie = champModifie; }

    public String getAncienneValeur() { return ancienneValeur; }
    public void setAncienneValeur(String ancienneValeur) { this.ancienneValeur = ancienneValeur; }

    public String getNouvelleValeur() { return nouvelleValeur; }
    public void setNouvelleValeur(String nouvelleValeur) { this.nouvelleValeur = nouvelleValeur; }

    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }
}