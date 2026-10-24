package ma.fsbm.lias_lab.security;

import ma.fsbm.lias_lab.entity.Mandat;
import ma.fsbm.lias_lab.entity.Membre;
import ma.fsbm.lias_lab.entity.StatutMembre;
import ma.fsbm.lias_lab.repository.MembreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private MembreRepository membreRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Membre membre = membreRepository.findByEmailWithMandats(email)
                .orElseThrow(() -> new UsernameNotFoundException("Aucun compte pour " + email));

        // §8 du CDC : RETRAITE = compte gelé, ANCIEN = compte désactivé
        boolean compteActif = membre.getStatut() != StatutMembre.RETRAITE
                            && membre.getStatut() != StatutMembre.ANCIEN
                            && membre.isActif();

        return new User(
                membre.getEmail(),
                membre.getMotDePasse(),
                compteActif,   // enabled
                true,          // accountNonExpired
                true,          // credentialsNonExpired
                compteActif,   // accountNonLocked
                buildAuthorities(membre)
        );
    }

    private Collection<? extends GrantedAuthority> buildAuthorities(Membre membre) {
        Set<GrantedAuthority> roles = new HashSet<>();

        // §5.1 : accès technique séparé de la gouvernance métier
        if (membre.isAdministrateurSysteme()) {
            roles.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        // §21 : STATUT (permanent/associé/doctorant) - notion séparée du rôle métier
        StatutMembre statut = membre.getStatut();
        if (statut != null) {
            switch (statut) {
                case PERMANENT: roles.add(new SimpleGrantedAuthority("ROLE_PERMANENT")); break;
                case ASSOCIE:   roles.add(new SimpleGrantedAuthority("ROLE_ASSOCIE")); break;
                case DOCTORANT: roles.add(new SimpleGrantedAuthority("ROLE_DOCTORANT")); break;
                default: break; // RETRAITE / ANCIEN : compte déjà bloqué au-dessus
            }
        }

        // §5.3 / §7 : RÔLE métier dérivé du mandat actif (jamais figé -> historisation §21)
        List<Mandat> mandats = membre.getMandats();
        if (mandats != null) {
            Mandat mandatActif = mandats.stream()
                    .filter(Objects::nonNull)
                    .filter(m -> m.getDateFin() == null || m.getDateFin().isAfter(LocalDate.now()))
                    .findFirst()
                    .orElse(null);

            if (mandatActif != null && mandatActif.getTypeRole() != null) {
                switch (mandatActif.getTypeRole()) {
                    case DIRECTEUR:      roles.add(new SimpleGrantedAuthority("ROLE_DIRECTEUR")); break;
                    case VICE_DIRECTEUR: roles.add(new SimpleGrantedAuthority("ROLE_VICE_DIRECTEUR")); break;
                    case CHEF_EQUIPE:    roles.add(new SimpleGrantedAuthority("ROLE_CHEF_EQUIPE")); break;
                    case MEMBRE_EQUIPE:  roles.add(new SimpleGrantedAuthority("ROLE_MEMBRE_EQUIPE")); break;
                }
            }
        }

        return roles;
    }
}