package ma.fsbm.lias_lab.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "publication")
public class Publication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    // Section 12 : "Ajout par membres et doctorants" — l'auteur doit être un vrai membre,
    // pas du texte libre, pour garantir la cohérence du classement
    @ManyToMany
    @JoinTable(
        name = "publication_auteurs",
        joinColumns = @JoinColumn(name = "publication_id"),
        inverseJoinColumns = @JoinColumn(name = "membre_id")
    )
    private List<Membre> auteurs = new ArrayList<>();

    private Integer annee;

    @ManyToOne
    @JoinColumn(name = "equipe_id")
    private Equipe equipe;

    @Column(columnDefinition = "TEXT")
    private String resume;

    private String lien;

    private boolean archive = false;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public List<Membre> getAuteurs() { return auteurs; }
    public void setAuteurs(List<Membre> auteurs) { this.auteurs = auteurs; }

    public Integer getAnnee() { return annee; }
    public void setAnnee(Integer annee) { this.annee = annee; }

    public Equipe getEquipe() { return equipe; }
    public void setEquipe(Equipe equipe) { this.equipe = equipe; }

    public String getResume() { return resume; }
    public void setResume(String resume) { this.resume = resume; }

    public String getLien() { return lien; }
    public void setLien(String lien) { this.lien = lien; }

    public boolean isArchive() { return archive; }
    public void setArchive(boolean archive) { this.archive = archive; }
}