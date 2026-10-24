package ma.fsbm.lias_lab.entity;

import java.util.List;

// Section 11 : objet calculé à la volée pour l'affichage du rapport annuel — jamais persisté
public class RapportAnnuel {

    private int annee;
    private List<Evenement> evenements;
    private List<Publication> publications;
    private List<Document> documents;
    private List<Reunion> reunions;
    private List<Convention> conventions;
    private List<DemandeMateriel> demandesMateriel;

    public int getAnnee() { return annee; }
    public void setAnnee(int annee) { this.annee = annee; }

    public List<Evenement> getEvenements() { return evenements; }
    public void setEvenements(List<Evenement> evenements) { this.evenements = evenements; }

    public List<Publication> getPublications() { return publications; }
    public void setPublications(List<Publication> publications) { this.publications = publications; }

    public List<Document> getDocuments() { return documents; }
    public void setDocuments(List<Document> documents) { this.documents = documents; }

    public List<Reunion> getReunions() { return reunions; }
    public void setReunions(List<Reunion> reunions) { this.reunions = reunions; }

    public List<Convention> getConventions() { return conventions; }
    public void setConventions(List<Convention> conventions) { this.conventions = conventions; }

    public List<DemandeMateriel> getDemandesMateriel() { return demandesMateriel; }
    public void setDemandesMateriel(List<DemandeMateriel> demandesMateriel) { this.demandesMateriel = demandesMateriel; }

    // Section 11 : synthèse chiffrée pour la justification budgétaire
    public long getNombreConventionsValides() {
        if (conventions == null) return 0;
        return conventions.stream().filter(c -> c.getDateFin() == null).count();
    }

    public long getNombreDemandesValidees() {
        if (demandesMateriel == null) return 0;
        return demandesMateriel.stream().filter(d -> d.getStatut() == StatutDemandeMateriel.VALIDEE).count();
    }
}