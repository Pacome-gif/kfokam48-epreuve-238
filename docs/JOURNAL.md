# Journal — 238

## Étape 1 — Analyse et conception (≈ 10h05 → 10h45)

**Fait** : cahier des charges (14 EF, 8 ENF, 18 RG, 10 hypothèses/contradictions tranchées), 4 diagrammes Mermaid (D1 cas d'utilisation, D2 modèle de données, D3 séquence présence, D4 états d'un exercice — bonus), contrat OpenAPI complété (5 opérations imposées + 9), 13 issues (7 Must, 5 Should, 1 Could) reliées aux EF/RG, puis `[JALON] analyse`.

**Bloqué** : ~15 min au démarrage : mon dossier personnel entier était déjà un dépôt Git et le dossier EPREUVE/ était vide ; j'ai créé un dépôt séparé et recréé le contrat depuis l'Annexe B. ~10 min pour installer et connecter `gh`. ~10 min sur Q10/Q15, tranchée en faveur de Q15 parce que le contrat impose `409 relecture déjà rendue`.

**IA** : Claude m'a proposé le cahier des charges, les diagrammes, le contrat et le backlog. Vérifié : chaque RG renvoie à une Qx de CLIENT.md, chaque code HTTP de D3 existe dans `contrat.yaml` (relu à la main), le YAML est parsé sans erreur (`python3 -c "import yaml…"`), et chaque titre d'issue décrit un résultat que le client comprendrait. J'ai relu la section 7 pour être capable de défendre chaque décision.

## Étape 2 — Version 0.1, stories Must (≈ 10h50 → 11h50)

**Fait** : les 7 issues Must (#1 à #7), avec une branche et une PR par issue (PR #14 à #20). Chaque PR a été fusionnée dans `main` par un merge commit et ferme son issue. Côté backend : Spring Boot 3.5, Flyway V1 (schéma conforme à D2) et V2 (données de démo), DTO, `@RestControllerAdvice`, et 277 tests dont un test unitaire RG1 avec une horloge figée, le tirage du relecteur (RG5) et des tests d'intégration MockMvc sur H2. Côté frontend : Next.js avec les 3 écrans et la couche `lib/api.ts`. Le démarrage se fait avec `docker compose up --build`.

**Bloqué** : ~30 min pour le premier build Docker, parce que le téléchargement des dépendances Maven était très lent sur le réseau de la salle ; j'ai continué à coder pendant ce temps. ~10 min sur un « Connection refused » au démarrage : le healthcheck `pg_isready` passait par le socket Unix et déclarait PostgreSQL prêt trop tôt. Je l'ai corrigé en TCP dans la PR #14 avant de la fusionner. Mon poste n'a que `docker-compose` v1 : le README indique les deux commandes.

**IA** : Claude a écrit la plupart du code à partir des critères des issues. Vérifications : `./mvnw test` au vert sur chaque branche, `npm run lint` et `npm run build`, puis un scénario `curl` complet sur la stack Docker avec PostgreSQL. Chaque code HTTP et chaque `{code, message}` ont été comparés au contrat : 201/409/410/400 pour les présences, 400/409 pour les exercices, 400/403/200/409 pour les relectures, 404 pour le tableau. J'ai aussi vérifié à la main que le tableau de démo donne 15 et 12 de moyenne, et « en attente » pour Carine.

## Étape 3 — Enveloppe : bug et changement de besoin (≈ 11h50 → 12h25)

**Fait** :
- **Bug** : issue #21 ouverte avant tout code, avec la cause et les étapes de reproduction. Commit `d416e0c` : le test seul, qui échoue. Correctif et script de reproduction dans la PR #24, sur une branche séparée.
- **Changement** : issues #22 et #23. Premier commit : l'analyse mise à jour (CDC v2, D1 à D4). Ensuite le contrat v1.1.0, puis la migration **V3 ajoutée** (V1 et V2 ne sont pas modifiées), puis le code. PR #25 et #26, séparées du correctif.

**Repriorisation (ce que je sacrifie)** : #8 (blocage après 5 erreurs), #11 (remplacer son lien) et #13 (détail par séance) sortent du périmètre, et #12 (voir sa note) passe Must. Les raisons sont dans le CDC §4 et en commentaire de chaque issue :
- le blocage est une protection, alors que le code expire déjà en 15 min ;
- avec deux relecteurs, la règle de Q13 devient ambiguë.

**Bloqué** :
- ~20 min à traduire le message du client : dans mon code, deux présences n'entrent jamais en conflit directement. En lisant les logs, j'ai trouvé la vraie cause : l'attribution d'un exercice en attente dans la même transaction que la présence, avec une violation de `uk_relecture_exercice`. Reproduit 15 fois sur 15.
- ~10 min sur `docker-compose` v1, qui plante quand il recrée un conteneur (`ContainerConfig`) : il faut supprimer l'ancien conteneur à la main.
- ~5 min : un commit dont les tests dépendaient des données du commit suivant. Je les ai fusionnés avant de pousser pour que chaque commit reste vert.

**IA** : Claude a proposé la cause du bug et le correctif (attribution après commit, dans sa propre transaction, avec verrou sur l'exercice). Vérifications :
1. Le test reproduit l'entrelacement et **échoue avant** le correctif, avec la même erreur que les logs de production.
2. Il passe après le correctif.
3. Le script sur PostgreSQL passe de 15 pertes sur 15 à 0 sur 15.

Pour la migration, j'ai appliqué V3 dans une transaction annulée sur la base remplie, puis laissé Flyway l'appliquer réellement : les 34 exercices et les 3 notes sont conservés. Une requête SQL confirme qu'aucun exercice n'a plus de 2 relecteurs, ni deux fois le même.

## Étape 4 — Version finale v1.0 (≈ 12h30 → 13h05)

**Fait** : lecture du sujet révisé, qui passe à 5 étapes (l'épreuve Git est supprimée) et utilise « issue » partout. La section 10 du CDC est mise à jour dans un commit dédié, sans toucher aux étapes passées. J'ai reformulé #9 et #10 au format « quand … alors … » avant de les coder, puis livré la présence ajoutée à la main (PR #27) et la clôture de séance (PR #28). Backlog trié : #8, #11 et #13 sont fermés « not planned », avec un renvoi à la repriorisation. J'ai aussi écrit le `CHANGELOG.md` (analyse, 0.1.0, 1.0.0), et vérifié le README depuis un **clone vierge** avec `docker compose up --build`. Au total, 343 tests verts.

**Bloqué** : ~35 min d'attente sur le build depuis le clone vierge. Compose v2 utilise BuildKit, qui ne réutilise pas le cache de l'ancien builder : toutes les dépendances Maven ont été retéléchargées sur le réseau lent. Mon premier essai avait en plus été interrompu par un `timeout` que j'avais mis moi-même. La durée réelle est indiquée dans le README.

**IA** : Claude a codé #9 et #10 et rédigé le CHANGELOG. Vérifications : les tests d'intégration couvrent les critères « quand … alors … » (201 FORMATEUR même code expiré, 409, 403, puis après clôture 410 pour la présence et le dépôt, et 200 pour une relecture). Chaque ligne du CHANGELOG a été contrôlée avec `git log --first-parent` (numéros d'issue, de PR et hash des jalons). Enfin, j'ai rejoué le parcours `curl` sur la stack lancée depuis le clone.
