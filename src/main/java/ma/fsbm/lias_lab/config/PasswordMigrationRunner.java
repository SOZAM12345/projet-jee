package ma.fsbm.lias_lab.config;

import ma.fsbm.lias_lab.repository.MembreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordMigrationRunner implements CommandLineRunner {

    @Autowired private MembreRepository membreRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        membreRepository.findAll().forEach(m -> {
            if (m.getMotDePasse() != null
                    && !m.getMotDePasse().startsWith("$2a$")
                    && !m.getMotDePasse().startsWith("$2b$")) {
                System.out.println("Migration du mot de passe pour : " + m.getEmail());
                m.setMotDePasse(passwordEncoder.encode(m.getMotDePasse()));
                membreRepository.save(m);
            }
        });
        System.out.println("Migration des mots de passe terminée.");
    }
}