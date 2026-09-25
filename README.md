# kfokam48-epreuve-KF48-238

Application de présence par code et de relecture par les pairs pour la formation KFOKAM48.

| Partie | Techno | Dossier |
|---|---|---|
| Backend | Java 17, Spring Boot 3.5, Maven (`mvnw`), PostgreSQL 16, Flyway | `backend/` |
| Frontend | **Next.js 16** (App Router, TypeScript) — choisi parce que je le pratique déjà et qu'il route les trois écrans sans dépendance supplémentaire | `frontend/` |
| Contrat d'API | OpenAPI 3 | `api/contrat.yaml` |
| Analyse | Cahier des charges, diagrammes Mermaid, journal | `docs/` |

## Démarrer (une commande)

Prérequis : Docker avec Compose.

```bash
git clone https://github.com/Pacome-gif/kfokam48-epreuve-KF48-238.git
cd kfokam48-epreuve-KF48-238
docker compose up --build
```

> Avec l'ancien Compose v1 : `docker-compose up --build`.
> Le premier build télécharge les dépendances Maven et npm (quelques minutes).

Ensuite :
- Frontend : http://localhost:3000
- API : http://localhost:8080/api/promotions

### Données de démonstration

Chargées automatiquement par la migration Flyway `V2__donnees_demo.sql` :
- **Promotion A** (6 étudiants) et **Promotion B** (4 étudiants) ;
- une séance passée « Séance 1 » (promotion A) avec 5 présences (dont une ajoutée par le formateur), 3 exercices, 2 relectures rendues et 1 en attente (exercices antérieurs à la double relecture : 1 relecteur requis) ;
- une « Séance 2 » (migration `V4__donnees_demo_double_relecture.sql`) avec **deux relecteurs par exercice** : Daniel a une note **provisoire** de 14 (1 relecture sur 2), Estelle une note définitive de 14,5 (13 et 16).

Le tableau de la promotion A n'est donc pas vide et montre les deux cas.

Pour essayer le parcours complet : écran **Formateur** → ouvrir une séance pour la promotion A → noter le code → écran **Étudiant** → marquer la présence de deux étudiants, puis en choisir un troisième qui marque sa présence et dépose un lien (il reçoit deux relecteurs) → écran **Relecteur** → rendre une première note → écran **Étudiant** : la note apparaît « provisoire » → rendre la seconde → la note devient définitive → retour au **tableau**.

Reproduction du bug corrigé #21 (deux présences simultanées) : `./scripts/repro-bug-21.sh` une fois la stack démarrée.

## Démarrer sans Docker (développement)

Prérequis : Java 17, Node 20+, un PostgreSQL local avec une base `kfokam48` (utilisateur et mot de passe `kfokam48`, modifiables via `DB_URL`, `DB_USER`, `DB_PASSWORD`).

```bash
cd backend && ./mvnw spring-boot:run          # API sur :8080
cd frontend && npm ci && npm run dev          # front sur :3000
```

## Tests

```bash
cd backend && ./mvnw test
```

Les tests utilisent H2 en mémoire (mode PostgreSQL) avec les mêmes migrations Flyway : aucune base locale n'est nécessaire.

## Organisation

- `backend/src/main/java/cm/kfokam48/presence/` : `controller` → `service` → `repository`, `dto` (seuls objets sérialisés), `domain` (entités JPA), `exception` (gestion centralisée `{code, message}`).
- `backend/src/main/resources/db/migration/` : migrations Flyway (le schéma n'est jamais généré par Hibernate).
- `frontend/src/lib/api.ts` : unique couche d'appel à l'API.
