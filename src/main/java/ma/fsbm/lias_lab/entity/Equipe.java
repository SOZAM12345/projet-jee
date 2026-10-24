package ma.fsbm.lias_lab.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "equipe")
public class Equipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Section 21 : remplace la suppression physique — une équipe archivée
    // garde son historique (membres passés, mandats de chef d'équipe) consultable
    private boolean archive = false;

    @OneToMany(mappedBy = "equipe")
    private List<Membre> membres = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isArchive() { return archive; }
    public void setArchive(boolean archive) { this.archive = archive; }

    public List<Membre> getMembres() { return membres; }
    public void setMembres(List<Membre> membres) { this.membres = membres; }
}