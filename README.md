# FlashCard SRS — application de révision par cartes

Application web de révision de vocabulaire basée sur la **répétition espacée** :
chaque carte revue avec succès revient à intervalle croissant (1 jour, puis 7, puis 30).

Projet réalisé dans le cadre du cours de développement front-end.

---

## Structure du dépôt

| Dossier | Contenu |
|---|---|
| `flashcard-frontend/` | **Application Angular 21** — la partie évaluée |
| `flashcard-backend/` | API REST Spring Boot 3.4 + PostgreSQL |

---

## Lancer le projet

### Prérequis

- Java 21
- Node.js 20 ou 22
- PostgreSQL 16, avec une base nommée `flashcardsdb`

### Base de données

Les paramètres de connexion se trouvent dans
`flashcard-backend/src/main/resources/application.properties`.
Adapter `spring.datasource.username` et `spring.datasource.password` si besoin.

Les tables sont créées automatiquement par Hibernate au premier démarrage
(`spring.jpa.hibernate.ddl-auto=update`).

### Back-end — port 8080

```bash
cd flashcard-backend
./gradlew bootRun
```

Documentation de l'API : http://localhost:8080/swagger-ui.html

### Front-end — port 4200

```bash
cd flashcard-frontend
npm install
npm start
```

Application : http://localhost:4200

---

## Fonctionnalités

**Comptes et sécurité**
- Inscription, connexion, déconnexion
- Authentification par jeton JWT
- Deux rôles : `USER` et `GESTIONNAIRE`
- Pages protégées selon le rôle

**Decks**
- Créer, modifier, supprimer ses paquets de cartes
- Trois visibilités : privée, publique (soumise à validation), officielle
- Consultation des decks officiels validés

**Flashcards**
- Un mot, plusieurs traductions, plusieurs phrases d'exemple par traduction
- Badge indiquant l'état de révision de chaque carte

**Révision**
- Session présentant uniquement les cartes dues du jour
- Recto / verso, auto-évaluation, barre de progression
- Prochaine échéance calculée par le serveur

**Administration** (rôle `GESTIONNAIRE`)
- Gestion des langues
- Validation ou rejet des decks soumis
- Gestion des utilisateurs

---

## Architecture du front-end

```
src/app/
├── components/     Les 6 écrans (login, register, decks, flashcards, review, admin)
├── services/       Appels HTTP vers l'API — seuls à contacter le serveur
├── dto/            Interfaces TypeScript décrivant les données échangées
├── guards/         Contrôle d'accès aux routes (connexion, rôle)
├── interceptors/   Ajout automatique du jeton + traitement des erreurs 401/403
├── app.routes.ts   Plan du site, avec chargement à la demande
└── app.config.ts   Configuration globale (routeur, HTTP, interceptor)
```

**Choix techniques**

- Composants **standalone**, sans NgModule
- **Chargement à la demande** : chaque écran est un bundle séparé
- Mode **zoneless** (pas de Zone.js) — la vue est rafraîchie explicitement
- Formulaires **template-driven** (`ngModel`)
- Le jeton est ajouté aux requêtes en un seul endroit, l'interceptor

**Répartition des responsabilités**

Le front-end gère l'affichage, la navigation et le dialogue avec l'API.
La logique métier — algorithme de répétition espacée, chiffrement des mots de
passe, génération et vérification du jeton, contrôle des autorisations — est
entièrement côté serveur.

---

## Limites connues

- Le jeton est stocké dans le `localStorage`. Un cookie `httpOnly` serait plus
  sûr vis-à-vis des injections de script.
- Les identifiants de base de données figurent en clair dans
  `application.properties`. Ils devraient être lus depuis des variables
  d'environnement.
- Pas de tests automatisés : les fichiers `.spec.ts` sont ceux générés par Angular.
- PrimeNG est déclaré comme dépendance mais n'est pas utilisé — l'interface est
  entièrement stylée en SCSS.
