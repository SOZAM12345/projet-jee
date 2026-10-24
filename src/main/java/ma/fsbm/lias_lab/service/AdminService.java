package ma.fsbm.lias_lab.service;

import ma.fsbm.lias_lab.entity.Membre;
import ma.fsbm.lias_lab.entity.ParametreLaboratoire;
import ma.fsbm.lias_lab.repository.ParametreLaboratoireRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class AdminService {

    @Autowired
    private MembreService membreService;

    @Autowired
    private ParametreLaboratoireRepository parametreRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String CARACTERES = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";

    // Section 5.1 : "Comptes utilisateurs" — réinitialisation de mot de passe
    public String reinitialiserMotDePasse(Long membreId) {
        Membre membre = membreService.getMembreById(membreId);
        String nouveauMotDePasse = genererMotDePasseTemporaire();
        membre.setMotDePasse(passwordEncoder.encode(nouveauMotDePasse));
        membreService.creerMembre(membre); // sauvegarde via le service existant
        return nouveauMotDePasse; // affiché une seule fois à l'admin, jamais stocké en clair
    }

    private String genererMotDePasseTemporaire() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(CARACTERES.charAt(random.nextInt(CARACTERES.length())));
        }
        return sb.toString();
    }

    // Section 5.1 : "Paramétrage"
    public ParametreLaboratoire getParametres() {
        return parametreRepository.findAll().stream()
                .findFirst()
                .orElseGet(() -> {
                    ParametreLaboratoire p = new ParametreLaboratoire();
                    p.setNomLaboratoire("LIAS");
                    p.setEtablissement("FSBM Casablanca");
                    return parametreRepository.save(p);
                });
    }

    public void sauvegarderParametres(ParametreLaboratoire parametres) {
        parametreRepository.save(parametres);
    }
}