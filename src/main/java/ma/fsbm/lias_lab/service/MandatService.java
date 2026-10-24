package ma.fsbm.lias_lab.service;

import ma.fsbm.lias_lab.entity.Mandat;
import ma.fsbm.lias_lab.entity.Membre;
import ma.fsbm.lias_lab.entity.TypeRole;
import ma.fsbm.lias_lab.repository.MandatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MandatService {

    @Autowired
    private MandatRepository mandatRepository;

    public List<Mandat> getAllMandats() {
        return mandatRepository.findAll();
    }

    public Mandat getMandatById(Long id) {
        return mandatRepository.findById(id).orElse(null);
    }

    public List<Mandat> getMandatsByMembre(Long membreId) {
        return mandatRepository.findByMembreIdOrderByDateDebutDesc(membreId);
    }

    public List<Mandat> getMandatsGouvernance() {
        return mandatRepository.findAll().stream()
                .filter(m -> m.getTypeRole() == TypeRole.DIRECTEUR || m.getTypeRole() == TypeRole.VICE_DIRECTEUR)
                .sorted((a, b) -> b.getDateDebut().compareTo(a.getDateDebut()))
                .toList();
    }

    public List<Mandat> getMandatsEquipe() {
        return mandatRepository.findAll().stream()
                .filter(m -> m.getTypeRole() == TypeRole.CHEF_EQUIPE || m.getTypeRole() == TypeRole.MEMBRE_EQUIPE)
                .sorted((a, b) -> b.getDateDebut().compareTo(a.getDateDebut()))
                .toList();
    }

    public Mandat creerMandat(Mandat mandat) {
        if (mandat.getDateDebut() == null) {
            mandat.setDateDebut(LocalDate.now());
        }

        if (mandat.getTypeRole() == TypeRole.DIRECTEUR || mandat.getTypeRole() == TypeRole.VICE_DIRECTEUR) {
            mandat.setEquipe(null);
        }

        // La fermeture automatique de l'ancien mandat actif ne s'applique
        // que si le nouveau mandat est lui-même actif (saisie "en cours", pas historique)
        boolean nouveauMandatActif = (mandat.getDateFin() == null);

        if (nouveauMandatActif) {
            if (mandat.getTypeRole() == TypeRole.DIRECTEUR || mandat.getTypeRole() == TypeRole.VICE_DIRECTEUR) {
                mandatRepository.findByTypeRoleAndDateFinIsNull(mandat.getTypeRole())
                        .ifPresent(actuel -> {
                            actuel.setDateFin(LocalDate.now());
                            mandatRepository.save(actuel);
                        });
            } else if (mandat.getTypeRole() == TypeRole.CHEF_EQUIPE && mandat.getEquipe() != null) {
                mandatRepository.findByTypeRoleAndEquipeIdAndDateFinIsNull(TypeRole.CHEF_EQUIPE, mandat.getEquipe().getId())
                        .ifPresent(actuel -> {
                            actuel.setDateFin(LocalDate.now());
                            mandatRepository.save(actuel);
                        });
            } else if (mandat.getTypeRole() == TypeRole.MEMBRE_EQUIPE && mandat.getMembre() != null) {
                mandatRepository.findByMembreIdAndTypeRoleAndDateFinIsNull(mandat.getMembre().getId(), TypeRole.MEMBRE_EQUIPE)
                        .ifPresent(actuel -> {
                            actuel.setDateFin(LocalDate.now());
                            mandatRepository.save(actuel);
                        });
            }
        }

        return mandatRepository.save(mandat);
    }

    public void cloturerMandat(Long id) {
        Mandat mandat = mandatRepository.findById(id).orElseThrow();
        mandat.setDateFin(LocalDate.now());
        mandatRepository.save(mandat);
    }

    public List<Membre> getMembresAvecMandatActif(TypeRole typeRole) {
        return mandatRepository.findAll().stream()
                .filter(m -> m.getTypeRole() == typeRole && m.getDateFin() == null)
                .map(Mandat::getMembre)
                .toList();
    }

    public Mandat getChefEquipeActif(Long equipeId) {
        return mandatRepository.findByTypeRoleAndEquipeIdAndDateFinIsNull(TypeRole.CHEF_EQUIPE, equipeId)
                .orElse(null);
    }
}