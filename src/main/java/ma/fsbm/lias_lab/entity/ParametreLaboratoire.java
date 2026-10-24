package ma.fsbm.lias_lab.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

// Section 5.1 : "Paramétrage" — une seule ligne en base, les infos globales du laboratoire
@Entity
@Table(name = "parametre_laboratoire")
public class ParametreLaboratoire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomLaboratoire;

    // Section 4.1 : "Date de création du laboratoire"
    private LocalDate dateCreation;

    private String etablissement;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomLaboratoire() { return nomLaboratoire; }
    public void setNomLaboratoire(String nomLaboratoire) { this.nomLaboratoire = nomLaboratoire; }

    public LocalDate getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDate dateCreation) { this.dateCreation = dateCreation; }

    public String getEtablissement() { return etablissement; }
    public void setEtablissement(String etablissement) { this.etablissement = etablissement; }
}