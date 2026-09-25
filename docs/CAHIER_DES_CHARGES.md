# Cahier des charges — KFOKAM48 Présence & Relecture

Auteur : KF48-238 · **Version 2 (étape 3 : bug #21 et changement « deux relecteurs »)** · Frontend choisi : **Next.js**, parce que je le pratique déjà (App Router, TypeScript) et qu'il donne en un seul outil le routage des trois écrans et un build de production vérifiable (`npm run build`).

---

## 1. Contexte et objectif

La direction de la formation KFOKAM48 suit aujourd'hui la présence et les exercices à la main. Le formateur ne sait pas facilement qui était là, qui a rendu son travail, ni qui attend encore une relecture.

L'application doit :
- remplacer l'appel par un **code de présence** que l'étudiant saisit lui-même, avec une durée de validité courte pour éviter la fraude ;
- collecter le **lien de l'exercice** de chaque étudiant pour une séance ;
- organiser une **relecture par les pairs** : chaque exercice est relu par un autre étudiant, qui lui donne une note et un commentaire ;
- donner au formateur un **tableau de suivi** par étudiant : présences, exercices déposés, moyenne des notes reçues, relectures qu'il doit encore faire.

## 2. Acteurs et rôles

| Acteur | Ce qu'il peut faire |
|---|---|
| **Formateur** | Ouvre une séance pour une promotion et obtient un code de présence ; ajoute une présence à la main (marquée « ajoutée par le formateur ») ; clôture une séance ; consulte le tableau de suivi d'une promotion. |
| **Étudiant** | Se choisit dans la liste des étudiants de sa promotion (pas de mot de passe, Q1) ; saisit le code pour marquer sa présence ; dépose ou remplace le lien de son exercice ; consulte la note et le commentaire reçus, sans le nom du relecteur. |
| **Relecteur** | C'est un étudiant présent à la séance, désigné par le système. **Chaque exercice a deux relecteurs distincts** (v2). Le relecteur consulte les exercices qu'il doit relire et rend une note entière de 0 à 20 avec un commentaire. |
| **Système** | Génère le code, contrôle son expiration, tire les deux relecteurs au hasard, calcule la note retenue (provisoire ou définitive) et la moyenne. |

## 3. Périmètre

**Inclus**
- Ouverture d'une séance avec génération d'un code de présence qui expire au bout de 15 minutes.
- Marquage de présence par code et présence ajoutée à la main par le formateur.
- Dépôt du lien d'exercice.
- Assignation automatique de **deux relecteurs** et saisie de la relecture ; note retenue = moyenne des deux, provisoire tant qu'une seule est rendue (v2).
- Clôture de la séance par le formateur.
- Tableau de suivi par promotion.
- Données de démonstration chargées au démarrage (promotions et étudiants).

**Exclu**
- Authentification, mots de passe, comptes utilisateurs (Q1). L'identité est déclarative.
- Gestion (création, modification) des promotions et des étudiants : ils viennent des données de démonstration.
- Hébergement ou vérification du contenu des exercices : on stocke seulement un lien.
- Notifications (e-mail, SMS), export PDF ou Excel.
- Plusieurs formateurs, gestion des droits par formateur.
- Mise en forme soignée (le rendu visuel n'est pas évalué).
- **Sortis du périmètre en v2** (voir la repriorisation, §4) : le blocage après 5 codes erronés (EF13), le remplacement du lien (EF11) et le détail des présences par séance (EF14).

## 4. Exigences fonctionnelles

| Réf | Exigence | Critère d'acceptation | Priorité |
|---|---|---|---|
| EF1 | Le formateur ouvre une séance pour une promotion | `POST /api/sessions {titre, promotionId}` renvoie `201` avec un `code` de 6 caractères et `expirationAt = ouvertureAt + 15 min` ; le code s'affiche sur l'écran formateur. Titre ou promotion absent → `400`. | Must |
| EF2 | L'étudiant marque sa présence avec le code | Avec un code valide, `POST /api/presences` renvoie `201` avec `source = ETUDIANT`, et la présence est comptée dans le tableau. Code inconnu → `400 CODE_INCONNU`, expiré → `410 CODE_EXPIRE`, déjà présent → `409 DEJA_PRESENT`. | Must |
| EF3 | L'étudiant dépose le lien de son exercice pour une séance | `POST /api/exercices` renvoie `201 {id, statut}`. Lien qui n'est pas une URL http(s) → `400 LIEN_INVALIDE`, deuxième dépôt → `409 EXERCICE_DEJA_DEPOSE`. | Must |
| EF4 | *(v2)* Le système assigne **deux relecteurs distincts** à chaque exercice déposé | Au dépôt, jusqu'à deux présents autres que l'auteur reçoivent chacun une relecture et l'exercice passe à `EN_ATTENTE_RELECTURE`. S'il manque des candidats, les relectures manquantes sont assignées aux présences suivantes (H1, H12). Jamais deux fois le même relecteur. | Must |
| EF5 | Le relecteur rend une note et un commentaire | `POST /api/relectures/{id}` avec `note` entière de 0 à 20 → `200`. *(v2)* L'exercice passe à `RELU` quand **toutes** ses relectures requises sont rendues. Note hors bornes ou décimale → `400`, auteur de l'exercice → `403`, relecture déjà rendue → `409`. | Must |
| EF6 | Le formateur consulte le tableau d'une promotion | `GET /api/tableau?promotionId=` renvoie une ligne par étudiant : `presences`, `exercicesDeposes`, `moyenne` (calculée par l'API, `null` s'il n'a aucune note), `relecturesEnAttente`. *(v2)* La moyenne porte sur les notes retenues (RG21) ; `moyenneProvisoire = true` si l'une d'elles est provisoire, et le tableau affiche « provisoire ». Promotion inconnue → `404`. | Must |
| EF7 | L'étudiant choisit son identité dans une liste | L'écran étudiant propose la liste des promotions puis des étudiants (`GET /api/promotions`, `GET /api/promotions/{id}/etudiants`). | Must |
| EF8 | Le relecteur voit les relectures qu'il doit faire | `GET /api/etudiants/{id}/relectures` renvoie les relectures qui lui sont assignées, avec le lien de l'exercice et sans le nom de l'auteur. | Must |
| EF9 | Le formateur ajoute une présence à la main | `POST /api/sessions/{id}/presences {etudiantId}` → `201` avec `source = FORMATEUR`. Le tableau et la liste des présences affichent « ajoutée par le formateur ». Fonctionne même après l'expiration du code. | Should |
| EF10 | Le formateur clôture une séance | `POST /api/sessions/{id}/cloture` → `200`. Ensuite, un dépôt ou un remplacement de lien → `410 SESSION_CLOTUREE`. | Should |
| ~~EF11~~ | ~~L'étudiant remplace le lien de son exercice~~ **(hors périmètre v2)** | `PUT /api/exercices/{id} {etudiantId, lien}` → `200` tant que l'exercice n'est pas `RELU` et que la séance n'est pas clôturée ; sinon `409 EXERCICE_DEJA_RELU` ou `410`. | ~~Should~~ Sorti |
| EF12 | L'étudiant consulte la note reçue | `GET /api/etudiants/{id}/exercices` renvoie, pour chaque exercice, le statut, *(v2)* la **note retenue** avec `noteProvisoire`, le nombre de relectures rendues et attendues, et les commentaires reçus, **sans aucun champ qui identifie un relecteur**. L'écran étudiant affiche « provisoire ». | ~~Should~~ **Must (v2)** |
| ~~EF13~~ | ~~Blocage après 5 codes erronés~~ **(hors périmètre v2)** | Après 5 codes inconnus consécutifs, toute tentative de cet étudiant renvoie `429 TROP_DE_TENTATIVES` pendant 2 minutes ; le compteur repart à zéro après un succès. | ~~Should~~ Sorti |
| ~~EF14~~ | ~~Détail de la présence par séance dans le tableau~~ **(hors périmètre v2)** | Le tableau peut afficher, pour chaque étudiant, la liste des séances où il était présent. | ~~Could~~ Sorti |

### Traçabilité exigences → backlog

| Issue | Priorité | Exigences / règles |
|---|---|---|
| #1 Socle et démarrage en une commande | Must | ENF4, ENF5, ENF6, EF7 (référentiel) |
| #2 Ouvrir une séance | Must | EF1, RG1 |
| #3 Marquer sa présence | Must | EF2, EF7, RG1–RG3, H10 |
| #4 Déposer un exercice | Must | EF3, RG10, RG12, RG13 |
| #5 Assigner un relecteur | Must | EF4, RG5–RG7, H1, H3 (v1 : un relecteur) |
| #6 Rendre une relecture | Must | EF5, EF8, RG5, RG8, RG9, RG18 |
| #7 Tableau du formateur | Must | EF6, RG14, RG17 |
| #8 Blocage après 5 erreurs | ~~Should~~ **hors périmètre** | EF13, RG4 |
| #9 Présence manuelle | Should | EF9, RG15 |
| #10 Clôture de séance | Should | EF10, RG3, RG10 |
| #11 Remplacer son lien | ~~Should~~ **hors périmètre** | EF11, RG11 |
| #12 Consulter sa note | ~~Should~~ **Must (v2)**, traité dans #23 | EF12, RG16, RG21 |
| #13 Détail des présences par séance | ~~Could~~ **hors périmètre** | EF14 |
| **#21** Bug : présences simultanées perdues | Must (bug) | EF2, RG2, ENF9 |
| **#22** Deux relecteurs par exercice | Must (v2) | EF4, EF5, RG6, RG20, H11, H12 |
| **#23** Note retenue et provisoire | Must (v2) | EF6, EF12, RG14, RG21 |

La v0.1 livre les Must (#1 à #7). Les Should seront traités en v1.0 selon le temps restant après l'enveloppe.

### Repriorisation après l'enveloppe (étape 3)

Le changement « deux relecteurs » est un **Must qui arrive tard**. Il touche la base, le contrat, le calcul des notes et deux écrans. Pour le livrer en v1.0 sans promettre ce qui ne sera pas tenu :

| Élément | Décision | Pourquoi |
|---|---|---|
| #22, #23 (deux relecteurs, note provisoire) | **Must**, traités en premier après le bug #21 | Exigence explicite du client, qui remplace Q6. |
| #12 Consulter sa note (EF12) | Should → **Must** | Le client veut que l'étudiant voie sa note « marquée provisoire » : sans cet écran, #23 n'a pas de sens. |
| #8 Blocage anti-devinette (EF13, RG4) | **Sorti** | C'est une protection, pas une fonction de notation. Le code expire déjà en 15 min (RG1), ce qui limite fortement la devinette. |
| #11 Remplacer son lien (EF11, RG11) | **Sorti** | Avec deux relecteurs, « tant que personne n'a commencé à relire » (Q13) devient ambigu quand un relecteur a rendu et l'autre non. Il faudrait une nouvelle règle, sans valeur suffisante pour ce délai. |
| #13 Détail par séance (EF14) | **Sorti** | Could. Le total `presences` couvre Q16. |
| #9 Présence manuelle, #10 Clôture | **Conservés** (Should, v1.0 si le temps le permet) | Q14 (`source = FORMATEUR`) et la clôture conditionnent RG3 et RG10. |

## 5. Exigences non fonctionnelles

| Réf | Exigence | Comment on la vérifie |
|---|---|---|
| ENF1 | Volumétrie : jusqu'à 10 promotions de 40 étudiants et 40 marquages de présence dans la même minute | Données de démo réalistes ; contraintes d'unicité en base : pas de doublon même en cas de double clic. |
| ENF2 | Usage mobile : l'étudiant marque sa présence depuis son téléphone | L'écran étudiant reste utilisable à 360 px de large, sans défilement horizontal (vérifié avec l'outil responsive du navigateur). |
| ENF3 | Temps de réponse : moins de 500 ms pour `POST /api/presences` et `GET /api/tableau` en local | Mesure avec `curl -w "%{time_total}"` sur les données de démo. |
| ENF4 | Erreurs lisibles et uniformes : toujours `{code, message}`, jamais de stack trace | Test d'intégration sur un cas d'erreur, plus des `curl` sur chaque code du contrat. |
| ENF5 | Démarrage chez un tiers en une commande, avec des données de démo | `docker compose up` depuis un clone vierge, en suivant uniquement le README. |
| ENF6 | Schéma de base versionné | Migrations Flyway commitées ; `ddl-auto=validate`. |
| ENF7 | Horodatage cohérent | Les dates sont stockées en UTC (`TIMESTAMP WITH TIME ZONE`) et renvoyées en ISO-8601. |
| ENF8 | Anonymat du relecteur vis-à-vis de l'étudiant relu (Q8) | Aucune réponse destinée à l'étudiant relu ne contient `relecteurId` ni le nom du relecteur. |
| ENF9 | *(v2, bug #21)* Deux étudiants qui marquent leur présence en même temps sont tous les deux enregistrés | `PresencesSimultaneesTest` (entrelacement déterministe) et `scripts/repro-bug-21.sh` : 0 perte sur 15 essais. |

## 6. Règles de gestion

| Réf | Règle | Source |
|---|---|---|
| RG1 | Le code de présence expire 15 minutes après l'ouverture de la séance. Au-delà : `410 CODE_EXPIRE`. | Q2 |
| RG2 | Un étudiant n'a qu'une seule présence par séance, quelle que soit sa source : `409 DEJA_PRESENT`. | Énoncé, Annexe B |
| RG3 | Aucune présence par code après la clôture de la séance : `410 SESSION_CLOTUREE`. | Q3 |
| ~~RG4~~ | ~~Après 5 codes inconnus consécutifs, l'étudiant est bloqué 2 minutes.~~ **Hors périmètre v2** (repriorisation §4). | Q4 |
| RG5 | Un étudiant ne relit jamais son propre exercice : exclu du tirage, et `403 AUTO_RELECTURE` si tentative. | Q5 |
| RG6 | *(v2, remplace Q6)* Un exercice a **deux relecteurs distincts** (`UNIQUE (exercice_id, relecteur_id)`). Les exercices déposés avant ce changement en gardent un seul (H11). | ~~Q6~~ Enveloppe |
| RG7 | Les relecteurs sont tirés au hasard parmi les étudiants **présents** à la séance, sauf l'auteur et sauf un relecteur déjà assigné à cet exercice, en priorité parmi ceux qui ont le moins de relectures dans cette séance. | Q7 + H3 |
| RG8 | La note est un entier de 0 à 20 inclus : sinon `400 NOTE_INVALIDE`. | Q9 |
| RG9 | Une relecture rendue est définitive : deuxième envoi → `409 RELECTURE_DEJA_RENDUE`. | Q15 (tranché contre Q10, voir C1) |
| RG10 | Un exercice peut être déposé tant que la séance n'est pas clôturée, même après l'expiration du code. | Q12 |
| ~~RG11~~ | ~~Le lien d'un exercice est remplaçable tant que sa relecture n'est pas rendue.~~ **Hors périmètre v2** : le lien n'est plus remplaçable. | Q13 |
| RG12 | Un seul exercice par étudiant et par séance : `409 EXERCICE_DEJA_DEPOSE`. | Annexe B |
| RG13 | Le lien doit être une URL absolue `http` ou `https` : `400 LIEN_INVALIDE`. | Annexe B |
| RG14 | *(v2)* La moyenne d'un étudiant est la moyenne des **notes retenues** (RG21) de ses exercices qui ont au moins une note, arrondie à 2 décimales, `null` s'il n'en a aucune. Elle est **provisoire** si l'une de ces notes l'est. Elle est calculée par l'API uniquement. | Q16, F3, enveloppe |
| RG15 | Une présence ajoutée par le formateur porte `source = FORMATEUR` ; elle n'est soumise ni à l'expiration du code ni au blocage. | Q14 |
| RG16 | L'étudiant relu voit sa note et le commentaire, jamais l'identité du relecteur. | Q8 |
| RG17 | Un exercice dont les relectures requises ne sont pas toutes rendues apparaît « en attente » dans le tableau du formateur. | Q11 |
| RG18 | Seul le relecteur assigné peut rendre une relecture : `403 NON_RELECTEUR`. | Hypothèse H2 |
| RG20 | *(v2)* Un exercice passe à `RELU` seulement quand toutes ses relectures requises sont rendues. | Enveloppe |
| RG21 | *(v2)* **Note retenue** d'un exercice = moyenne des notes rendues, arrondie à 2 décimales. Elle est **provisoire** tant que le nombre de relectures rendues est inférieur au nombre requis. Sans relecture rendue, pas de note. | Enveloppe |

## 7. Zones d'ombre, hypothèses et contradictions

| Point | Réponse client (Qx) ou hypothèse | Décision retenue | Pourquoi |
|---|---|---|---|
| **C1 — Contradiction** : correction d'une note | Q10 : « oui, tant que la séance n'est pas clôturée ». Q15 : « non, une fois validée c'est fini ». | **Q15 retenue** (RG9) : la relecture rendue est définitive. | Le contrat imposé prévoit `409 relecture déjà rendue` sur `POST /api/relectures/{id}` : il ne laisse pas de place à une correction. Q15 donne aussi la raison métier (« plus honnête »). |
| **C2 — Tension** : remplacer son lien (Q13) et `409 exercice déjà déposé` (contrat) | Q13 autorise le remplacement ; le contrat interdit le second `POST`. | Le `POST` reste strict (409). Le remplacement passe par une opération à part, `PUT /api/exercices/{id}` (EF11). | On respecte le contrat à la lettre sans perdre le besoin de Q13. |
| **H1 — Le trou** : pas de relecteur possible | Q7 : tirage parmi les présents. Mais un exercice peut être déposé quand aucun autre étudiant n'est présent, par exemple le premier dépôt, ou le soir (Q12). Aucune question ne le prévoit. | L'assignation se fait au dépôt. S'il n'y a aucun candidat, l'exercice reste `DEPOSE`, puis il est assigné dès qu'une nouvelle présence est enregistrée dans la séance. Le tableau le montre « en attente » (RG17). | Aucun exercice n'est perdu, et on respecte Q5, Q6 et Q7. |
| **H2 — Identité du relecteur** | Q1 : pas de mot de passe. Or `POST /api/relectures/{id}` ne transporte que `{note, commentaire}` : comment savoir qui envoie, et donc produire le `403` ? | L'identité déclarée passe par l'en-tête `X-Etudiant-Id`. `403 AUTO_RELECTURE` s'il s'agit de l'auteur, `403 NON_RELECTEUR` s'il ne s'agit pas du relecteur assigné. | On garde le corps imposé par le contrat et on rend le `403` vérifiable, dans l'esprit de Q1. |
| **H3 — Équité du tirage** | Q7 : « au hasard ». Un tirage purement aléatoire peut donner 3 relectures au même étudiant. | Tirage au hasard parmi les présents qui ont le moins de relectures dans la séance. | La charge reste répartie, et le tirage reste aléatoire. |
| **H4 — « Commencé à relire » (Q13)** | L'application n'a pas d'état « relecture en cours ». | « Commencé » = relecture rendue. Le lien est remplaçable tant que l'exercice n'est pas `RELU`. | C'est le seul événement observable. Le relecteur voit toujours le lien actuel. |
| **H5 — Dépôt sans être présent** | Q12 : dépôt le soir, faute de connexion. La présence n'est pas mentionnée. | La présence n'est pas exigée pour déposer. | Q12 vise les étudiants qui n'ont pas de connexion : on ne les pénalise pas deux fois. |
| **H6 — Fin de séance et clôture** | Q3 parle de « fin de la séance », Q10 et Q12 de « clôture ». | « Fin » = expiration du code (RG1) ; « clôture » = action explicite du formateur (EF10). Après la clôture, plus de présence par code, de dépôt ni de remplacement. Une relecture peut encore être rendue. | Sinon des exercices resteraient « en attente » pour toujours. |
| **H7 — Blocage (Q4)** | Qu'est-ce qu'une « erreur » ? | Seul un code **inconnu** compte (c'est la devinette). Un code expiré ou une présence en double ne comptent pas. Code HTTP `429`, qui n'est pas dans le contrat. | `429 Too Many Requests` est le code standard pour une limitation de fréquence. |
| **H8 — « Présence à chaque séance » (Q16)** vs contrat `presences` | Le contrat définit un seul champ `presences`. | `presences` = nombre de séances où l'étudiant est présent. Le détail par séance est reporté (EF14, Could). | On respecte le contrat. Q16 est couvert au niveau du total. |
| **H9 — Q11 dans le tableau** | Le contrat ne prévoit pas de champ « exercices en attente ». | Ajout du champ `exercicesEnAttente` à chaque ligne du tableau. Les champs imposés restent inchangés. | Ajouter un champ ne casse pas le contrat, et Q11 demande que ce soit « clairement » visible. |
| **H10 — Étudiant d'une autre promotion** | Rien n'est dit. | Présence refusée si l'étudiant n'appartient pas à la promotion de la séance : `403 HORS_PROMOTION`. | Cela évite qu'un code partagé serve à une autre promotion. |
| **C3 — Changement de besoin (enveloppe)** : deux relecteurs | Q6 : « un seul ». Enveloppe : « deux pairs différents, moyenne, provisoire si un seul a rendu ». | La demande la plus récente l'emporte : RG6 est remplacée, RG20 et RG21 sont créées, RG14 est modifiée. | Le client a constaté en usage réel qu'un relecteur défaillant prive l'étudiant de note. |
| **H11 — Données existantes** | L'enveloppe dit « à partir de maintenant ». | Migration V3 : les exercices existants gardent `relecteurs_requis = 1` ; les nouveaux en demandent 2. | La base remplie survit sans modification des notes déjà rendues, qui restent définitives. |
| **H12 — Un seul candidat présent** | Même trou que H1, avec deux relecteurs. | Le candidat disponible est assigné tout de suite ; le second relecteur l'est à la prochaine présence. | Une première note, même provisoire, arrive au plus tôt. |
| **H13 — Note retenue décimale** | Chaque note est entière (Q9), mais la moyenne de 13 et 14 vaut 13,5. | La note retenue et la moyenne sont décimales (2 chiffres). La note **rendue** par un relecteur reste entière (RG8). | Arrondir à l'entier trahirait la moyenne demandée. |
| **H14 — Concurrence (bug #21)** | Deux présences simultanées pouvaient attribuer le même exercice : la seconde présence était annulée. | L'attribution se fait après la validation de la présence, dans sa propre transaction ; l'exercice est verrouillé pendant l'attribution (`SELECT … FOR UPDATE`) pour ne jamais dépasser deux relecteurs. | Une présence ne doit jamais dépendre d'un traitement secondaire. |
| **Q-inutiles** | Q1 (confort), Q9 (reprise de l'énoncé) | Pris en compte sans impact particulier. | — |

## 8. Contraintes techniques

- **Backend** : Java 17, Spring Boot 3, Maven avec wrapper `mvnw` commité (B1).
- **Contrat** : `api/contrat.yaml` respecté à la lettre, avec le format d'erreur `{code, message}` (B2).
- **Couches** contrôleur, service et repository ; DTO en entrée comme en sortie, aucune entité JPA sérialisée (B3).
- **Validation** Bean Validation et `@RestControllerAdvice` centralisé, sans stack trace renvoyée au client (B4).
- **Schéma** versionné par Flyway, `ddl-auto=validate` (B5). Base PostgreSQL 16 ; H2 en mode PostgreSQL pour les tests.
- **Tests** : un test unitaire sur une règle métier et un test d'intégration sur un endpoint, qui tournent sans base locale (B6).
- **Frontend** Next.js, avec les appels API dans `frontend/src/lib/api.ts` uniquement et sans aucun calcul métier (F1–F3).
- **Démarrage** : `docker compose up` (PostgreSQL, backend, frontend).
- **Git** : dépôt public, une branche et une PR par ticket, jalons `[JALON]`.

## 9. Livrables

- `docs/CAHIER_DES_CHARGES.md` (ce document), `docs/JOURNAL.md`, `docs/diagrammes/` (D1 à D4, en Mermaid).
- `api/contrat.yaml` (OpenAPI 3).
- `backend/` : Spring Boot, avec les migrations Flyway et les tests.
- `frontend/` : Next.js, avec les trois écrans (formateur, étudiant, relecteur).
- `docker-compose.yml`, `README.md` (installation testée depuis un clone vierge), `CHANGELOG.md`.
- Le backlog en issues GitHub, les PR liées, les jalons `[JALON] analyse`, `[JALON] v0.1` et `[JALON] v1.0`.
- `SOUMISSION.md`, déposé sur la plateforme.

## 10. Démarche prévue

1. **Analyse** : ce cahier des charges, les diagrammes, le contrat et le backlog en issues, puis `[JALON] analyse`. Aucun code avant ce jalon.
2. **v0.1** : les stories Must uniquement. Une branche `feat/<n>-<slug>` par issue, des commits atomiques qui citent les EF et RG concernées, une PR qui ferme l'issue (`Closes #n`). `main` compile et ses tests passent à chaque fusion. Puis `[JALON] v0.1`.
3. **Enveloppe** : ouvrir une issue pour le bug et une autre pour l'évolution **avant** de coder ; reproduire le bug par un test ; nouvelle migration Flyway ; mise à jour du contrat, du cahier des charges et des diagrammes dans un commit dédié ; correctif et évolution sur deux branches séparées.
4. **v1.0** : les stories Should selon le temps restant, le CHANGELOG, un README vérifié depuis un clone vierge, le backlog restant trié. Puis `[JALON] v1.0`.
5. **Épreuve Git** : dans un dépôt séparé.
6. **Soumission** : les hash finaux relevés après le dernier push.

**Definition of Done** : un ticket est terminé quand ses critères d'acceptation sont vérifiés (par un test ou par un `curl` décrit dans la PR), que le code est fusionné dans `main` par une PR qui ferme l'issue, que `./mvnw test` et `npm run build` passent, et que le contrat et la documentation sont à jour si le ticket les touche.
