package ma.fsbm.lias_lab.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reunion")
public class Reunion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    private LocalDate dateReunion;

    private LocalTime heureReunion;

    // Section 17 : "Ordre du jour"
    @Column(columnDefinition = "TEXT")
    private String ordreDuJour;

    // Section 17 : "Upload des PV" — nullable, car une réunion peut être créée
    // (avec son ordre du jour) avant que le PV ne soit rédigé et uploadé
    private String cheminPv;

    @ManyToOne
    @JoinColumn(name = "equipe_id")
    private Equipe equipe; // optionnel : réunion d'une équipe précise ou générale

    @ManyToMany
    @JoinTable(
        name = "reunion_participants",
        joinColumns = @JoinColumn(name = "reunion_id"),
        inverseJoinColumns = @JoinColumn(name = "membre_id")
    )
    private List<Membre> participants = new ArrayList<>();

    // Section 21 : pas de suppression physique, seulement archivage
    private boolean archive = false;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public LocalDate getDateReunion() { return dateReunion; }
    public void setDateReunion(LocalDate dateReunion) { this.dateReunion = dateReunion; }

    public LocalTime getHeureReunion() { return heureReunion; }
    public void setHeureReunion(LocalTime heureReunion) { this.heureReunion = heureReunion; }

    public String getOrdreDuJour() { return ordreDuJour; }
    public void setOrdreDuJour(String ordreDuJour) { this.ordreDuJour = ordreDuJour; }

    public String getCheminPv() { return cheminPv; }
    public void setCheminPv(String cheminPv) { this.cheminPv = cheminPv; }

    public Equipe getEquipe() { return equipe; }
    public void setEquipe(Equipe equipe) { this.equipe = equipe; }

    public List<Membre> getParticipants() { return participants; }
    public void setParticipants(List<Membre> participants) { this.participants = participants; }

    public boolean isArchive() { return archive; }
    public void setArchive(boolean archive) { this.archive = archive; }
}