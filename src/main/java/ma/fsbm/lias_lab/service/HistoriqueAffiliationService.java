package ma.fsbm.lias_lab.service;

import ma.fsbm.lias_lab.entity.HistoriqueAffiliation;
import ma.fsbm.lias_lab.entity.Membre;
import ma.fsbm.lias_lab.repository.HistoriqueAffiliationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class HistoriqueAffiliationService {

    @Autowired
    private HistoriqueAffiliationRepository historiqueAffiliationRepository;

    // Ouvre une nouvelle période — appelée uniquement par MembreService (création, réactivation)
    public void ouvrirPeriode(Membre membre, LocalDate dateDebut, String motifOuverture) {
        HistoriqueAffiliation periode = new HistoriqueAffiliation();
        periode.setMembre(membre);
        periode.setDateDebut(dateDebut != null ? dateDebut : LocalDate.now());
        periode.setMotifOuverture(motifOuverture);
        historiqueAffiliationRepository.save(periode);
    }

    // Ferme la période en cours — appelée uniquement par MembreService (désactivation, retraite)
    // Ne touche jamais motifOuverture : le "pourquoi ça a commencé" reste intact
    public void cloturerPeriodeEnCours(Long membreId, String motifCloture) {
        List<HistoriqueAffiliation> periodes = historiqueAffiliationRepository
                .findByMembreIdOrderByDateDebutDesc(membreId);

        periodes.stream()
                .filter(p -> p.getDateFin() == null)
                .findFirst()
                .ifPresent(p -> {
                    p.setDateFin(LocalDate.now());
                    p.setMotifCloture(motifCloture);
                    historiqueAffiliationRepository.save(p);
                });
    }

    public List<HistoriqueAffiliation> getHistoriqueByMembre(Long membreId) {
        return historiqueAffiliationRepository.findByMembreIdOrderByDateDebutDesc(membreId);
    }
}