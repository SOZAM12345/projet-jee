package ma.fsbm.lias_lab.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "evenement")
public class Evenement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Enumerated(EnumType.STRING)
    private TypeEvenement type;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String lieu;

    // Section 9 : "Archivage par édition" — regroupe les éditions successives
    // d'un même événement récurrent (ex: "2025", "Édition 2026")
    private String edition;

    // Section 21 : rien n'est supprimé, seulement archivé
    private boolean archive = false;

    // Section 9 : "Affectation d'organisateurs" — liés à de vrais membres
    @ManyToMany
    @JoinTable(
        name = "evenement_organisateurs",
        joinColumns = @JoinColumn(name = "evenement_id"),
        inverseJoinColumns = @JoinColumn(name = "membre_id")
    )
    private List<Membre> organisateurs = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public TypeEvenement getType() { return type; }
    public void setType(TypeEvenement type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }

    public String getEdition() { return edition; }
    public void setEdition(String edition) { this.edition = edition; }

    public boolean isArchive() { return archive; }
    public void setArchive(boolean archive) { this.archive = archive; }

    public List<Membre> getOrganisateurs() { return organisateurs; }
    public void setOrganisateurs(List<Membre> organisateurs) { this.organisateurs = organisateurs; }
}