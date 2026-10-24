package ma.fsbm.lias_lab.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "membre")
public class Membre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String motDePasse;

    @Enumerated(EnumType.STRING)
    private StatutMembre statut;

    private String etablissementOrigine;
    private String laboratoireOrigine;

    @Column(columnDefinition = "TEXT")
    private String biographie;

    @Column(columnDefinition = "TEXT")
    private String centresInteret;

    private LocalDate dateNaissance;
    private LocalDate dateEmbauche;
    private LocalDate dateAffiliation;

    private boolean actif = true;

    private String cheminPhoto;

    // Section 5.1 : accès technique, indépendant du statut métier et des mandats
    private boolean administrateurSysteme = false;

    @ManyToOne
    @JoinColumn(name = "equipe_id")
    private Equipe equipe;

    @OneToMany(mappedBy = "membre")
    private List<Mandat> mandats;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    public StatutMembre getStatut() { return statut; }
    public void setStatut(StatutMembre statut) { this.statut = statut; }

    public String getEtablissementOrigine() { return etablissementOrigine; }
    public void setEtablissementOrigine(String etablissementOrigine) { this.etablissementOrigine = etablissementOrigine; }

    public String getLaboratoireOrigine() { return laboratoireOrigine; }
    public void setLaboratoireOrigine(String laboratoireOrigine) { this.laboratoireOrigine = laboratoireOrigine; }

    public String getBiographie() { return biographie; }
    public void setBiographie(String biographie) { this.biographie = biographie; }

    public String getCentresInteret() { return centresInteret; }
    public void setCentresInteret(String centresInteret) { this.centresInteret = centresInteret; }

    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }

    public LocalDate getDateEmbauche() { return dateEmbauche; }
    public void setDateEmbauche(LocalDate dateEmbauche) { this.dateEmbauche = dateEmbauche; }

    public LocalDate getDateAffiliation() { return dateAffiliation; }
    public void setDateAffiliation(LocalDate dateAffiliation) { this.dateAffiliation = dateAffiliation; }

    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }

    public String getCheminPhoto() { return cheminPhoto; }
    public void setCheminPhoto(String cheminPhoto) { this.cheminPhoto = cheminPhoto; }

    public boolean isAdministrateurSysteme() { return administrateurSysteme; }
    public void setAdministrateurSysteme(boolean administrateurSysteme) { this.administrateurSysteme = administrateurSysteme; }

    public Equipe getEquipe() { return equipe; }
    public void setEquipe(Equipe equipe) { this.equipe = equipe; }

    public List<Mandat> getMandats() { return mandats; }
    public void setMandats(List<Mandat> mandats) { this.mandats = mandats; }
}