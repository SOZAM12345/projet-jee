package ma.fsbm.lias_lab.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "partenaire")
public class Partenaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    private String typeOrganisme; // ex: Université, Entreprise, Organisme public...

    private String pays;

    private String contactEmail;

    @Column(columnDefinition = "TEXT")
    private String description;

    private boolean archive = false;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getTypeOrganisme() { return typeOrganisme; }
    public void setTypeOrganisme(String typeOrganisme) { this.typeOrganisme = typeOrganisme; }

    public String getPays() { return pays; }
    public void setPays(String pays) { this.pays = pays; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isArchive() { return archive; }
    public void setArchive(boolean archive) { this.archive = archive; }
}