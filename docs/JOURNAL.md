# Journal — KF48-238

## Étape 1 — Analyse et conception (≈ 10h05 → 10h45)

**Fait** : cahier des charges (14 EF, 8 ENF, 18 RG, 10 hypothèses/contradictions tranchées), 4 diagrammes Mermaid (D1 cas d'utilisation, D2 modèle de données, D3 séquence présence, D4 états d'un exercice — bonus), contrat OpenAPI complété (5 opérations imposées + 9), 13 issues (7 Must, 5 Should, 1 Could) reliées aux EF/RG, puis `[JALON] analyse`.

**Bloqué** : ~15 min au démarrage : mon dossier personnel entier était déjà un dépôt Git et le dossier EPREUVE/ était vide ; j'ai créé un dépôt séparé et recréé le contrat depuis l'Annexe B. ~10 min pour installer et connecter `gh`. ~10 min sur Q10/Q15, tranchée en faveur de Q15 parce que le contrat impose `409 relecture déjà rendue`.

**IA** : Claude m'a proposé le cahier des charges, les diagrammes, le contrat et le backlog. Vérifié : chaque RG renvoie à une Qx de CLIENT.md, chaque code HTTP de D3 existe dans `contrat.yaml` (relu à la main), le YAML est parsé sans erreur (`python3 -c "import yaml…"`), et chaque titre d'issue décrit un résultat que le client comprendrait. J'ai relu la section 7 pour être capable de défendre chaque décision.

## Étape 2 — Version 0.1, stories Must (≈ 10h50 → 11h50)

**Fait** : les 7 issues Must (#1 à #7), avec une branche et une PR par issue (PR #14 à #20). Chaque PR a été fusionnée dans `main` par un merge commit et ferme son issue. Côté backend : Spring Boot 3.5, Flyway V1 (schéma conforme à D2) et V2 (données de démo), DTO, `@RestControllerAdvice`, et 277 tests dont un test unitaire RG1 avec une horloge figée, le tirage du relecteur (RG5) et des tests d'intégration MockMvc sur H2. Côté frontend : Next.js avec les 3 écrans et la couche `lib/api.ts`. Le démarrage se fait avec `docker compose up --build`.

**Bloqué** : ~30 min pour le premier build Docker, parce que le téléchargement des dépendances Maven était très lent sur le réseau de la salle ; j'ai continué à coder pendant ce temps. ~10 min sur un « Connection refused » au démarrage : le healthcheck `pg_isready` passait par le socket Unix et déclarait PostgreSQL prêt trop tôt. Je l'ai corrigé en TCP dans la PR #14 avant de la fusionner. Mon poste n'a que `docker-compose` v1 : le README indique les deux commandes.

**IA** : Claude a écrit la plupart du code à partir des critères des issues. Vérifications : `./mvnw test` au vert sur chaque branche, `npm run lint` et `npm run build`, puis un scénario `curl` complet sur la stack Docker avec PostgreSQL. Chaque code HTTP et chaque `{code, message}` ont été comparés au contrat : 201/409/410/400 pour les présences, 400/409 pour les exercices, 400/403/200/409 pour les relectures, 404 pour le tableau. J'ai aussi vérifié à la main que le tableau de démo donne 15 et 12 de moyenne, et « en attente » pour Carine.
