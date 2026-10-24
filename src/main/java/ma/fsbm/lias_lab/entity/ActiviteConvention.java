package ma.fsbm.lias_lab.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

// Section 16 : "Suivi des activités associées" à une convention
// (ex: échange de chercheurs, projet commun, événement co-organisé)
@Entity
@Table(name = "activite_convention")
public class ActiviteConvention {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "convention_id", nullable = false)
    private Convention convention;

    @Column(nullable = false)
    private String description;

    private LocalDate date;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Convention getConvention() { return convention; }
    public void setConvention(Convention convention) { this.convention = convention; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}