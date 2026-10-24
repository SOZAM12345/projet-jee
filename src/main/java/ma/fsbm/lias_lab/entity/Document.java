package ma.fsbm.lias_lab.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "document")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeDocument type;

    @Column(nullable = false)
    private String cheminFichier;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Section 10 : "Structuration : par événement et par date"
    @ManyToOne
    @JoinColumn(name = "evenement_id")
    private Evenement evenement; // nullable : un document administratif général peut ne pas être lié à un événement

    @ManyToOne
    @JoinColumn(name = "uploader_id")
    private Membre uploader;

    private LocalDateTime dateUpload = LocalDateTime.now();

    // Section 21 : pas de suppression physique, seulement archivage
    private boolean archive = false;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public TypeDocument getType() { return type; }
    public void setType(TypeDocument type) { this.type = type; }

    public String getCheminFichier() { return cheminFichier; }
    public void setCheminFichier(String cheminFichier) { this.cheminFichier = cheminFichier; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Evenement getEvenement() { return evenement; }
    public void setEvenement(Evenement evenement) { this.evenement = evenement; }

    public Membre getUploader() { return uploader; }
    public void setUploader(Membre uploader) { this.uploader = uploader; }

    public LocalDateTime getDateUpload() { return dateUpload; }
    public void setDateUpload(LocalDateTime dateUpload) { this.dateUpload = dateUpload; }

    public boolean isArchive() { return archive; }
    public void setArchive(boolean archive) { this.archive = archive; }
}