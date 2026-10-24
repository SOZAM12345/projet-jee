package ma.fsbm.lias_lab.service;

import ma.fsbm.lias_lab.entity.Equipe;
import ma.fsbm.lias_lab.entity.HistoriqueModificationMembre;
import ma.fsbm.lias_lab.entity.Membre;
import ma.fsbm.lias_lab.entity.StatutMembre;
import ma.fsbm.lias_lab.repository.EquipeRepository;
import ma.fsbm.lias_lab.repository.HistoriqueModificationMembreRepository;
import ma.fsbm.lias_lab.repository.MembreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MembreService {

    @Autowired
    private MembreRepository membreRepository;

    @Autowired
    private HistoriqueModificationMembreRepository historiqueRepository;

    @Autowired
    private HistoriqueAffiliationService historiqueAffiliationService;

    @Autowired
    private EquipeRepository equipeRepository;

    // Nécessaire pour hasher le mot de passe à la création (bean déjà défini dans SecurityConfig)
    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Membre> getAllMembres() {
        return membreRepository.findAll();
    }

    public Membre getMembreById(Long id) {
        return membreRepository.findById(id).orElse(null);
    }

    // Section 4.3 : la création ouvre la première période d'affiliation
    public Membre creerMembre(Membre membre) {
        // Correction : le mot de passe saisi dans le formulaire arrive en clair.
        // Il DOIT être hashé avant d'être stocké, sinon Spring Security (BCrypt)
        // ne pourra jamais authentifier ce compte, quel que soit le mot de passe tapé.
        if (membre.getMotDePasse() != null && !membre.getMotDePasse().isBlank()) {
            membre.setMotDePasse(passwordEncoder.encode(membre.getMotDePasse()));
        }

        Membre sauvegarde = membreRepository.save(membre);
        historiqueAffiliationService.ouvrirPeriode(sauvegarde, sauvegarde.getDateAffiliation(), "Adhésion initiale");
        return sauvegarde;
    }

    public Membre modifierMembre(Long id, Membre nouvellesDonnees) {
        Membre existant = membreRepository.findById(id).orElseThrow();

        comparerEtHistoriser(existant, "Nom", existant.getNom(), nouvellesDonnees.getNom());
        existant.setNom(nouvellesDonnees.getNom());

        comparerEtHistoriser(existant, "Prénom", existant.getPrenom(), nouvellesDonnees.getPrenom());
        existant.setPrenom(nouvellesDonnees.getPrenom());

        comparerEtHistoriser(existant, "Email", existant.getEmail(), nouvellesDonnees.getEmail());
        existant.setEmail(nouvellesDonnees.getEmail());

        String ancienStatut = existant.getStatut() != null ? existant.getStatut().name() : null;
        String nouveauStatut = nouvellesDonnees.getStatut() != null ? nouvellesDonnees.getStatut().name() : null;
        comparerEtHistoriser(existant, "Statut", ancienStatut, nouveauStatut);
        existant.setStatut(nouvellesDonnees.getStatut());

        comparerEtHistoriser(existant, "Établissement d'origine", existant.getEtablissementOrigine(), nouvellesDonnees.getEtablissementOrigine());
        existant.setEtablissementOrigine(nouvellesDonnees.getEtablissementOrigine());

        comparerEtHistoriser(existant, "Laboratoire d'origine", existant.getLaboratoireOrigine(), nouvellesDonnees.getLaboratoireOrigine());
        existant.setLaboratoireOrigine(nouvellesDonnees.getLaboratoireOrigine());

        comparerEtHistoriser(existant, "Biographie", existant.getBiographie(), nouvellesDonnees.getBiographie());
        existant.setBiographie(nouvellesDonnees.getBiographie());

        comparerEtHistoriser(existant, "Centres d'intérêt", existant.getCentresInteret(), nouvellesDonnees.getCentresInteret());
        existant.setCentresInteret(nouvellesDonnees.getCentresInteret());

        // Date d'affiliation : historisée séparément car c'est une clé métier (section 4.2)
        String avantAffiliation = existant.getDateAffiliation() != null ? existant.getDateAffiliation().toString() : "";
        String apresAffiliation = nouvellesDonnees.getDateAffiliation() != null ? nouvellesDonnees.getDateAffiliation().toString() : "";
        comparerEtHistoriser(existant, "Date d'affiliation", avantAffiliation, apresAffiliation);
        existant.setDateAffiliation(nouvellesDonnees.getDateAffiliation());

        existant.setDateEmbauche(nouvellesDonnees.getDateEmbauche());
        existant.setDateNaissance(nouvellesDonnees.getDateNaissance());

        // NOTE : l'équipe n'est PAS reprise ici. Elle est gérée séparément par
        // affecterEquipe(), appelée depuis le contrôleur avec le paramètre equipeId.
        // (Avant, "existant.setEquipe(nouvellesDonnees.getEquipe())" écrasait
        // l'équipe à chaque modification du profil, car le formulaire ne l'envoie pas.)

        // Mot de passe et photo volontairement exclus : gérés séparément pour ne jamais être écrasés par erreur
        return membreRepository.save(existant);
    }

    private void comparerEtHistoriser(Membre membre, String champ, String ancienne, String nouvelle) {
        String a = ancienne != null ? ancienne : "";
        String n = nouvelle != null ? nouvelle : "";
        if (!a.equals(n)) {
            HistoriqueModificationMembre h = new HistoriqueModificationMembre();
            h.setMembre(membre);
            h.setChampModifie(champ);
            h.setAncienneValeur(a);
            h.setNouvelleValeur(n);
            historiqueRepository.save(h);
        }
    }

    public void setCheminPhoto(Long id, String cheminPhoto) {
        Membre m = membreRepository.findById(id).orElseThrow();
        m.setCheminPhoto(cheminPhoto);
        membreRepository.save(m);
    }

    // Section 3 : "Équipe actuelle" — gérée à part, comme la photo, avec historisation
    public void affecterEquipe(Long membreId, Long equipeId) {
        Membre m = membreRepository.findById(membreId).orElseThrow();

        String avant = (m.getEquipe() != null) ? m.getEquipe().getNom() : "";
        Equipe nouvelle = (equipeId == null) ? null : equipeRepository.findById(equipeId).orElse(null);
        String apres = (nouvelle != null) ? nouvelle.getNom() : "";

        if (!avant.equals(apres)) {
            m.setEquipe(nouvelle);
            membreRepository.save(m);
            comparerEtHistoriser(m, "Équipe", avant.isEmpty() ? "—" : avant, apres.isEmpty() ? "—" : apres);
        }
    }

    public List<HistoriqueModificationMembre> getHistoriqueModifications(Long membreId) {
        return historiqueRepository.findByMembreIdOrderByDateModificationDesc(membreId);
    }

    // Section 8 + 21 : désactivation au lieu de suppression physique — ferme la période d'affiliation en cours
    public void desactiverMembre(Long id) {
        Membre m = membreRepository.findById(id).orElseThrow();
        m.setActif(false);
        m.setStatut(StatutMembre.ANCIEN);
        membreRepository.save(m);
        historiqueAffiliationService.cloturerPeriodeEnCours(id, "Désactivation - ancien membre");
    }

    // Section 8 : "peut éventuellement être réactivé" — rouvre une nouvelle période d'affiliation
    public void reactiverMembre(Long id, StatutMembre nouveauStatut) {
        Membre m = membreRepository.findById(id).orElseThrow();
        m.setActif(true);
        m.setStatut(nouveauStatut);
        membreRepository.save(m);
        historiqueAffiliationService.ouvrirPeriode(m, null, "Réintégration");
    }

    // Section 8 : Retraité = compte gelé (reste actif=true, mais le statut RETRAITE sert de marqueur)
    public void mettreEnRetraite(Long id) {
        Membre m = membreRepository.findById(id).orElseThrow();
        m.setStatut(StatutMembre.RETRAITE);
        membreRepository.save(m);
        historiqueAffiliationService.cloturerPeriodeEnCours(id, "Mise en retraite");
    }
    public Membre getMembreByEmail(String email) {
        return membreRepository.findByEmail(email).orElse(null);
    }
    public List<Membre> getMembresByIds(List<Long> ids) {
        return membreRepository.findAllById(ids);
    }
}