package ma.fsbm.lias_lab.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/css/**", "/js/**", "/images/**", "/uploads/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/equipes", "/equipes/{id:[0-9]+}").permitAll()
                .requestMatchers(HttpMethod.GET, "/evenements", "/evenements/{id:[0-9]+}").permitAll()
                .requestMatchers(HttpMethod.GET, "/calendrier").permitAll()
                .requestMatchers(HttpMethod.GET, "/publications").permitAll()
                .requestMatchers("/demandes/soumettre", "/demandes/enregistrer", "/demandes/merci", "/demandes/statut").permitAll()
                .requestMatchers("/dashboard").authenticated()
                .requestMatchers(HttpMethod.GET, "/membres/{id:[0-9]+}").authenticated()
                .requestMatchers(HttpMethod.GET, "/membres/{id:[0-9]+}/historique").authenticated()
                .requestMatchers("/membres/desactiver/**", "/membres/retraite/**", "/membres/reactiver/**")
                    .hasRole("DIRECTEUR")

                // §14 : consultation du stock et gestion des arrivages réservées à la direction ;
                // chaque membre ne voit que ce qui lui a été distribué personnellement (route à part, avant la règle générale)
                .requestMatchers("/materiel/mes-distributions")
                    .authenticated()
                .requestMatchers("/materiel/**")
                    .hasAnyRole("DIRECTEUR", "VICE_DIRECTEUR")

                .requestMatchers("/membres/**", "/reunions/**", "/conventions/**", "/partenaires/**")
                    .hasAnyRole("PERMANENT", "DIRECTEUR", "VICE_DIRECTEUR", "CHEF_EQUIPE", "MEMBRE_EQUIPE")

                .requestMatchers("/documents/**")
                    .hasAnyRole("PERMANENT", "DIRECTEUR", "VICE_DIRECTEUR", "CHEF_EQUIPE", "MEMBRE_EQUIPE", "ASSOCIE")

                // §15 : "Validation par direction" — la liste complète et l'historique des demandes
                // ne concernent que la direction ; soumettre et consulter ses propres demandes
                // reste ouvert à tout membre actif (doctorants inclus). Ces deux règles doivent
                // rester AVANT la règle générale "/demandes-materiel/**" ci-dessous.
                .requestMatchers(HttpMethod.GET, "/demandes-materiel", "/demandes-materiel/historique")
                    .hasAnyRole("DIRECTEUR", "VICE_DIRECTEUR")
                .requestMatchers("/demandes-materiel/valider/**", "/demandes-materiel/refuser/**")
                    .hasRole("DIRECTEUR")
                .requestMatchers("/demandes-materiel/soumettre", "/demandes-materiel/enregistrer", "/demandes-materiel/mes-demandes")
                    .authenticated()
                .requestMatchers("/demandes-materiel/**")
                    .hasAnyRole("PERMANENT", "DIRECTEUR", "VICE_DIRECTEUR", "CHEF_EQUIPE", "MEMBRE_EQUIPE")

                .requestMatchers("/equipes/**")
                    .hasAnyRole("PERMANENT", "DIRECTEUR", "VICE_DIRECTEUR", "CHEF_EQUIPE", "MEMBRE_EQUIPE")
                .requestMatchers("/evenements/**")
                    .hasAnyRole("PERMANENT", "DIRECTEUR", "VICE_DIRECTEUR", "CHEF_EQUIPE", "MEMBRE_EQUIPE")
                .requestMatchers("/publications/ajouter", "/publications/enregistrer").authenticated()
                .requestMatchers("/publications/archiver/**", "/publications/desarchiver/**")
                    .hasAnyRole("PERMANENT", "DIRECTEUR", "VICE_DIRECTEUR", "CHEF_EQUIPE", "MEMBRE_EQUIPE")
                .requestMatchers("/mandats/ajouter", "/mandats/enregistrer", "/mandats/cloturer/**")
                    .hasRole("DIRECTEUR")
                .requestMatchers("/mandats", "/mandats/membre/**")
                    .hasAnyRole("PERMANENT", "DIRECTEUR", "VICE_DIRECTEUR", "CHEF_EQUIPE", "MEMBRE_EQUIPE")
                .requestMatchers("/demandes/**", "/rapport-annuel/**")
                    .hasRole("DIRECTEUR")

                .requestMatchers("/recherche")
                    .hasAnyRole("PERMANENT", "DIRECTEUR", "VICE_DIRECTEUR", "CHEF_EQUIPE", "MEMBRE_EQUIPE", "ASSOCIE")

                .requestMatchers("/notifications/**")
                    .authenticated()

                // Discussions privées : ouvertes aussi à l'admin système, pour coordination technique
                // avec la direction (ex: création de compte), mais pas les discussions d'équipe/événement
                .requestMatchers("/discussions", "/discussions/avec/**")
                    .hasAnyRole("PERMANENT", "DIRECTEUR", "VICE_DIRECTEUR", "CHEF_EQUIPE", "MEMBRE_EQUIPE", "ASSOCIE", "ADMIN")
                .requestMatchers("/discussions/**")
                    .hasAnyRole("PERMANENT", "DIRECTEUR", "VICE_DIRECTEUR", "CHEF_EQUIPE", "MEMBRE_EQUIPE", "ASSOCIE")

                // --- Administration système (§5.1) : rôle technique, séparé de la gouvernance métier ---
                .requestMatchers("/admin/**")
                    .hasRole("ADMIN")
                // ------------------------------------------------------------------------------------

                .anyRequest().authenticated()
            )
            .addFilterAfter(new CsrfTokenEagerLoaderFilter(), CsrfFilter.class)
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }
}