# 🧪 LIAS-LAB — Application de gestion de laboratoire

## 📌 Présentation du projet

LIAS-LAB est une application web développée dans le cadre du module **JEE & Frameworks** du Master Big Data & Data Science.

Ce projet vise à faciliter la gestion et l'organisation des activités d'un laboratoire à travers une plateforme web centralisée.

L'application permet de gérer différentes ressources et activités administratives du laboratoire.

## 🎯 Objectifs

- Centraliser les informations du laboratoire.
- Faciliter le suivi des membres et des équipes.
- Organiser les réunions et les événements.
- Gérer les demandes et la distribution du matériel.
- Améliorer la gestion des documents et des activités.

## ⚙️ Fonctionnalités principales

L'application comprend notamment des modules pour :

- **Authentification et administration** : accès et gestion administrative.
- **Gestion des membres** : consultation et suivi des membres du laboratoire.
- **Gestion des équipes** : organisation des équipes.
- **Gestion du matériel** : demandes et suivi de la distribution.
- **Réunions et événements** : organisation et consultation.
- **Documents et publications** : gestion des informations associées.
- **Conventions et partenariats** : suivi des collaborations.
- **Tableau de bord** : accès aux informations de gestion.

## 🛠️ Technologies utilisées

| Technologie | Utilisation |
|---|---|
| Java | Développement de l'application |
| Spring Boot | Framework backend |
| Spring Data JPA / Hibernate | Persistance des données |
| MySQL | Base de données relationnelle |
| HTML / Thymeleaf | Interfaces web |
| Maven | Gestion des dépendances |
| Eclipse IDE | Environnement de développement |
| Git et GitHub | Gestion de versions |

## 📁 Structure du projet

```text
lias-lab/
├── .mvn/
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   │       ├── static/
│   │       ├── templates/
│   │       └── application.properties
│   └── test/
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

## 🚀 Installation et exécution

### Prérequis

- JDK 17
- MySQL
- Maven (ou Maven Wrapper inclus)
- Eclipse IDE ou un autre IDE Java

### 1. Cloner le dépôt

```bash
git clone https://github.com/SOZAM12345/projet-jee.git
cd projet-jee
```

### 2. Configurer MySQL

Créer une base de données :

```sql
CREATE DATABASE lias_lab;
```

Configurer les paramètres de connexion dans `src/main/resources/application.properties`.

Définir la variable d'environnement `DB_PASSWORD` avec le mot de passe MySQL local. Ne jamais publier les identifiants de connexion.

### 3. Lancer l'application

Sous Windows :

```cmd
mvnw.cmd spring-boot:run
```

L'application utilise le port **8081** par défaut.

Accès local : http://localhost:8081

## 🔒 Sécurité

Les identifiants et mots de passe de connexion à la base de données doivent être configurés localement et ne doivent pas être publiés dans le dépôt GitHub.

## 🎓 Contexte académique

- **Projet :** Application JEE de gestion de laboratoire
- **Module :** JEE & Frameworks
- **Formation :** Master Big Data & Data Science
- **Établissement :** Faculté des Sciences Ben M'Sik — Université Hassan II
- **Réalisé par :** Salma Zamakhchari

## 👩‍💻 Auteure

**Salma Zamakhchari**

GitHub : [@SOZAM12345](https://github.com/SOZAM12345)

---

*Projet académique réalisé dans le cadre du Master Big Data & Data Science.*
