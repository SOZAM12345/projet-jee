package ma.fsbm.lias_lab.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "convention")
public class Convention {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @ManyToOne
    @JoinColumn(name = "partenaire_id", nullable = false)
    private Partenaire partenaire;

    @Column(columnDefinition = "TEXT")
    private String objet; // objet de la convention

    private LocalDate dateSignature;
    private LocalDate dateFin; // null = durée indéterminée / en cours

    // Section 16 : "Archivage des documents" — le fichier de la convention signée
    private String cheminDocument;

    private boolean archive = false;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public Partenaire getPartenaire() { return partenaire; }
    public void setPartenaire(Partenaire partenaire) { this.partenaire = partenaire; }

    public String getObjet() { return objet; }
    public void setObjet(String objet) { this.objet = objet; }

    public LocalDate getDateSignature() { return dateSignature; }
    public void setDateSignature(LocalDate dateSignature) { this.dateSignature = dateSignature; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

    public String getCheminDocument() { return cheminDocument; }
    public void setCheminDocument(String cheminDocument) { this.cheminDocument = cheminDocument; }

    public boolean isArchive() { return archive; }
    public void setArchive(boolean archive) { this.archive = archive; }
}