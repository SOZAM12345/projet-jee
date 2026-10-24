package ma.fsbm.lias_lab.repository;

import ma.fsbm.lias_lab.entity.Mandat;
import ma.fsbm.lias_lab.entity.TypeRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MandatRepository extends JpaRepository<Mandat, Long> {

    List<Mandat> findByMembreIdOrderByDateDebutDesc(Long membreId);

    Optional<Mandat> findByTypeRoleAndDateFinIsNull(TypeRole typeRole);

    Optional<Mandat> findByTypeRoleAndEquipeIdAndDateFinIsNull(TypeRole typeRole, Long equipeId);

    Optional<Mandat> findByMembreIdAndTypeRoleAndDateFinIsNull(Long membreId, TypeRole typeRole);
}