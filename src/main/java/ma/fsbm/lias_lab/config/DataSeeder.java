package ma.fsbm.lias_lab.config;

import jakarta.persistence.EntityManager;
import ma.fsbm.lias_lab.entity.*;
import ma.fsbm.lias_lab.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
public class DataSeeder implements CommandLineRunner {

    @Value("${app.seed-demo-data:false}")
    private boolean seedDemoData;

    @Autowired private MembreRepository membreRepository;
    @Autowired private MandatRepository mandatRepository;
    @Autowired private EquipeRepository equipeRepository;
    @Autowired private DemandeMaterielRepository demandeMaterielRepository;
    @Autowired private DistributionMaterielRepository distributionMaterielRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private EntityManager entityManager;

    @Override
    @Transactional
    public void run(String... args) {
        if (!seedDemoData) {
            return;
        }

        System.out.println(">>> DataSeeder : remise à zéro complète de la base <<<");

        // 1. Tables de liaison @ManyToMany qui référencent membre.id
        entityManager.createNativeQuery("DELETE FROM evenement_organisateurs").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM publication_auteurs").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM reunion_participants").executeUpdate();

        // 1bis. Messages (§13) et notifications (§19) : référencent membre/équipe/événement
        entityManager.createNativeQuery("DELETE FROM message").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM notification").executeUpdate();

        // 2. Tables métier qui référencent membre.id ou equipe.id
        entityManager.createNativeQuery("DELETE FROM distribution_materiel").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM demande_materiel").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM mandat").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM document").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM reunion").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM publication").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM evenement").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM historique_affiliation").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM historique_modification_membre").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM demande_adhesion").executeUpdate();

        // 3. Membres et équipes en dernier
        entityManager.createNativeQuery("DELETE FROM membre").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM equipe").executeUpdate();

        entityManager.flush();

        // 4. Réinitialisation des compteurs AUTO_INCREMENT (MySQL)
        // Sans ça, les ID grimpent indéfiniment à chaque relance du seeder,
        // ce qui rend les tests et les captures d'écran illisibles.
        entityManager.createNativeQuery("ALTER TABLE evenement_organisateurs AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE publication_auteurs AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE reunion_participants AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE message AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE notification AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE distribution_materiel AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE demande_materiel AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE mandat AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE document AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE reunion AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE publication AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE evenement AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE historique_affiliation AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE historique_modification_membre AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE demande_adhesion AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE membre AUTO_INCREMENT = 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE equipe AUTO_INCREMENT = 1").executeUpdate();

        System.out.println(">>> DataSeeder : base vidée, création des 10 comptes de test <<<");

        // Équipes
        Equipe dataScience = new Equipe();
        dataScience.setNom("Data Science & IA");
        dataScience.setDescription("Modélisation, apprentissage automatique et traitement de données massives.");
        equipeRepository.save(dataScience);

        Equipe reseaux = new Equipe();
        reseaux.setNom("Réseaux & Systèmes");
        reseaux.setDescription("Infrastructures réseau, sécurité et systèmes distribués.");
        equipeRepository.save(reseaux);

        String motDePasse = passwordEncoder.encode("Test@1234");

        // #1 Directeur en mandat actif
        Membre directeur = creerMembre("Bennani", "Karim", "directeur@lias.com", motDePasse,
                StatutMembre.PERMANENT,
                "Faculté des Sciences Ben M'Sick", null,
                "Professeur habilité, directeur du laboratoire LIAS depuis 2023. Spécialiste en systèmes intelligents et gouvernance de la recherche.",
                "Gouvernance scientifique, intelligence artificielle, management de la recherche",
                LocalDate.of(1975, 3, 14), LocalDate.of(2005, 9, 1), LocalDate.of(2005, 9, 1),
                dataScience, "https://ui-avatars.com/api/?name=Karim+Bennani&background=1B3A63&color=fff&size=256");
        creerMandat(directeur, TypeRole.DIRECTEUR, null, LocalDate.of(2023, 1, 1), null);

        // #2 Vice-directrice en mandat actif
        Membre viceDirectrice = creerMembre("Idrissi", "Salma", "vicedirecteur@lias.com", motDePasse,
                StatutMembre.PERMANENT,
                "Faculté des Sciences Ben M'Sick", null,
                "Maîtresse de conférences, vice-directrice du laboratoire. Travaille sur le traitement du langage naturel.",
                "Traitement du langage naturel, éthique de l'IA",
                LocalDate.of(1980, 7, 22), LocalDate.of(2010, 9, 1), LocalDate.of(2010, 9, 1),
                dataScience, "https://ui-avatars.com/api/?name=Salma+Idrissi&background=A8752E&color=fff&size=256");
        creerMandat(viceDirectrice, TypeRole.VICE_DIRECTEUR, null, LocalDate.of(2023, 1, 1), null);

        // #3 Chef d'équipe en mandat actif
        Membre chefEquipe = creerMembre("Amrani", "Youssef", "chefequipe@lias.com", motDePasse,
                StatutMembre.PERMANENT,
                "Faculté des Sciences Ben M'Sick", null,
                "Chercheur permanent, chef de l'équipe Réseaux & Systèmes depuis 2024.",
                "Sécurité informatique, systèmes distribués",
                LocalDate.of(1985, 11, 5), LocalDate.of(2015, 9, 1), LocalDate.of(2015, 9, 1),
                reseaux, "https://ui-avatars.com/api/?name=Youssef+Amrani&background=2F6F6B&color=fff&size=256");
        creerMandat(chefEquipe, TypeRole.CHEF_EQUIPE, reseaux, LocalDate.of(2024, 1, 1), null);

        // #4 Membre d'équipe (rôle de gouvernance de base, mandat actif)
        Membre membreEquipe = creerMembre("Cherkaoui", "Nadia", "membreequipe@lias.com", motDePasse,
                StatutMembre.PERMANENT,
                "Faculté des Sciences Ben M'Sick", null,
                "Chercheuse permanente au sein de l'équipe Réseaux & Systèmes.",
                "Réseaux de capteurs, IoT",
                LocalDate.of(1990, 2, 18), LocalDate.of(2019, 9, 1), LocalDate.of(2019, 9, 1),
                reseaux, "https://ui-avatars.com/api/?name=Nadia+Cherkaoui&background=8A4B3B&color=fff&size=256");
        creerMandat(membreEquipe, TypeRole.MEMBRE_EQUIPE, reseaux, LocalDate.of(2024, 1, 1), null);

        // #5 Permanent sans rôle de gouvernance
        creerMembre("Fassi", "Omar", "permanent@lias.com", motDePasse,
                StatutMembre.PERMANENT,
                "Faculté des Sciences Ben M'Sick", null,
                "Chercheur permanent, ne détient actuellement aucun mandat de gouvernance.",
                "Bases de données, systèmes d'information",
                LocalDate.of(1988, 5, 30), LocalDate.of(2018, 9, 1), LocalDate.of(2018, 9, 1),
                dataScience, "https://ui-avatars.com/api/?name=Omar+Fassi&background=4B5D71&color=fff&size=256");

        // #6 Associé
        creerMembre("Tazi", "Leila", "associe@lias.com", motDePasse,
                StatutMembre.ASSOCIE,
                "Université Mohammed V, Rabat", "Laboratoire LRIT",
                "Chercheuse associée en collaboration avec le LIAS dans le cadre d'un projet commun.",
                "Vision par ordinateur, robotique",
                LocalDate.of(1983, 9, 9), null, LocalDate.of(2022, 1, 1),
                null, "https://ui-avatars.com/api/?name=Leila+Tazi&background=7E5720&color=fff&size=256");

        // #7 Doctorant
        creerMembre("Berrada", "Anas", "doctorant@lias.com", motDePasse,
                StatutMembre.DOCTORANT,
                "Faculté des Sciences Ben M'Sick", null,
                "Doctorant en 2e année, travaille sur l'apprentissage profond appliqué à l'imagerie médicale.",
                "Apprentissage profond, imagerie médicale",
                LocalDate.of(1997, 4, 12), null, LocalDate.of(2024, 9, 1),
                dataScience, "https://ui-avatars.com/api/?name=Anas+Berrada&background=1B3A63&color=fff&size=256");

        // #8 Retraité — compte gelé, aucun accès actif
        Membre retraite = creerMembre("Alaoui", "Fatima Zahra", "retraite@lias.com", motDePasse,
                StatutMembre.RETRAITE,
                "Faculté des Sciences Ben M'Sick", null,
                "Ancienne directrice du laboratoire, partie à la retraite en 2022.",
                "Systèmes experts, histoire du laboratoire",
                LocalDate.of(1958, 6, 1), LocalDate.of(1988, 9, 1), LocalDate.of(1988, 9, 1),
                null, "https://ui-avatars.com/api/?name=Fatima+Zahra+Alaoui&background=8B97A6&color=fff&size=256");
        retraite.setActif(false);
        membreRepository.save(retraite);

        // #9 Ancien membre — compte désactivé, réactivable
        Membre ancien = creerMembre("Ouazzani", "Rachid", "ancien@lias.com", motDePasse,
                StatutMembre.ANCIEN,
                "Faculté des Sciences Ben M'Sick", null,
                "A quitté le laboratoire en 2021 pour rejoindre le secteur privé.",
                "Génie logiciel",
                LocalDate.of(1992, 10, 25), LocalDate.of(2016, 9, 1), LocalDate.of(2016, 9, 1),
                null, "https://ui-avatars.com/api/?name=Rachid+Ouazzani&background=8B97A6&color=fff&size=256");
        ancien.setActif(false);
        membreRepository.save(ancien);

        // #10 Administrateur système — accès technique séparé de la gouvernance métier (§5.1)
        Membre admin = new Membre();
        admin.setNom("Rami");
        admin.setPrenom("Yassine");
        admin.setEmail("admin@lias.com");
        admin.setMotDePasse(motDePasse);
        admin.setActif(true);
        admin.setAdministrateurSysteme(true);
        admin.setCheminPhoto("https://ui-avatars.com/api/?name=Yassine+Rami&background=333333&color=fff&size=256");
        membreRepository.save(admin);

        System.out.println(">>> DataSeeder : 10 comptes de test créés (mot de passe : Test@1234) <<<");
    }

    private Membre creerMembre(String nom, String prenom, String email, String motDePasse,
                                StatutMembre statut, String etablissement, String labo,
                                String bio, String centres,
                                LocalDate naissance, LocalDate embauche, LocalDate affiliation,
                                Equipe equipe, String photo) {
        Membre m = new Membre();
        m.setNom(nom);
        m.setPrenom(prenom);
        m.setEmail(email);
        m.setMotDePasse(motDePasse);
        m.setStatut(statut);
        m.setEtablissementOrigine(etablissement);
        m.setLaboratoireOrigine(labo);
        m.setBiographie(bio);
        m.setCentresInteret(centres);
        m.setDateNaissance(naissance);
        m.setDateEmbauche(embauche);
        m.setDateAffiliation(affiliation);
        m.setActif(true);
        m.setCheminPhoto(photo);
        m.setEquipe(equipe);
        return membreRepository.save(m);
    }

    private void creerMandat(Membre membre, TypeRole role, Equipe equipe, LocalDate debut, LocalDate fin) {
        Mandat mandat = new Mandat();
        mandat.setMembre(membre);
        mandat.setTypeRole(role);
        mandat.setEquipe(equipe);
        mandat.setDateDebut(debut);
        mandat.setDateFin(fin);
        mandatRepository.save(mandat);
    }
}