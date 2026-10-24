package ma.fsbm.lias_lab.entity;

import java.util.List;

// Section 18 : conteneur des résultats de recherche globale — jamais persisté
public class ResultatRecherche {

    private String motCle;
    private List<Membre> membres;
    private List<Document> documents;
    private List<Evenement> evenements;
    private List<Publication> publications;

    public String getMotCle() { return motCle; }
    public void setMotCle(String motCle) { this.motCle = motCle; }

    public List<Membre> getMembres() { return membres; }
    public void setMembres(List<Membre> membres) { this.membres = membres; }

    public List<Document> getDocuments() { return documents; }
    public void setDocuments(List<Document> documents) { this.documents = documents; }

    public List<Evenement> getEvenements() { return evenements; }
    public void setEvenements(List<Evenement> evenements) { this.evenements = evenements; }

    public List<Publication> getPublications() { return publications; }
    public void setPublications(List<Publication> publications) { this.publications = publications; }

    public int getTotalResultats() {
        return membres.size() + documents.size() + evenements.size() + publications.size();
    }
}