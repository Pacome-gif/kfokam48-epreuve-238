# Changelog

Toutes les évolutions notables du projet. Format inspiré de [Keep a Changelog](https://keepachangelog.com/fr/1.1.0/).
Chaque entrée renvoie à son issue (#n) et à sa pull request (PR #n). Les versions correspondent aux commits jalons de l'historique.

## [1.0.0] — 2026-09-25 — jalon `[JALON] v1.0`

### Corrigé
- **Deux présences simultanées : l'une était perdue** (#21, PR #24). L'attribution d'un relecteur à un exercice en attente se faisait dans la même transaction que la présence : en cas de conflit sur `uk_relecture_exercice`, la présence était annulée (500). L'attribution a désormais lieu après la validation de la présence, dans sa propre transaction. Le bug est prouvé par `PresencesSimultaneesTest` (commit `d416e0c`, en échec avant le correctif) et par `scripts/repro-bug-21.sh` (15 pertes sur 15 avant, 0 sur 15 après).

### Modifié — changement de besoin (enveloppe, étape 3)
- **Deux relecteurs distincts par exercice** (#22, PR #25) : cette règle remplace Q6 / RG6. La migration `V3__deux_relecteurs.sql` est ajoutée (V1 et V2 ne sont pas modifiées) : contrainte `UNIQUE (exercice_id, relecteur_id)` et colonne `exercice.relecteurs_requis`. Les exercices existants gardent 1 relecteur requis (H11). L'exercice passe à `RELU` quand toutes ses relectures sont rendues (RG20).
- **Note retenue = moyenne des relectures, provisoire tant qu'une seule est rendue** (#23, #12, PR #26) : nouveau champ `moyenneProvisoire` dans `GET /api/tableau`, et nouvelle vue étudiant `GET /api/etudiants/{id}/exercices` (EF12, passée Must). Données de démo `V4`.
- Contrat d'API **v1.1.0**. Cahier des charges v2 et diagrammes D1 à D4 mis à jour dans un commit dédié (`d0f40c4`).

### Ajouté
- **Le formateur ajoute une présence à la main**, marquée « ajoutée par le formateur » (#9, PR #27) : `POST` et `GET /api/sessions/{id}/presences`.
- **Le formateur clôture une séance** (#10, PR #28) : `POST /api/sessions/{id}/cloture`. Après la clôture, les présences par code et les dépôts sont refusés (410), mais les relectures restent possibles.

### Retiré du périmètre (repriorisation écrite, CDC §4)
- Blocage après 5 codes erronés (#8, RG4), remplacement du lien d'exercice (#11, RG11) et détail des présences par séance (#13). Les issues sont fermées comme « not planned ». `PUT /api/exercices/{id}` et `429` ont été retirés du contrat.

## [0.1.0] — 2026-09-25 — jalon `[JALON] v0.1` (`5af75d7`)

### Ajouté — stories Must
- Socle : Spring Boot 3.5 / Java 17, Flyway (`V1` schéma, `V2` données de démo), gestion d'erreurs `{code, message}`, Next.js, `docker compose` (#1, PR #14).
- Le formateur ouvre une séance et obtient un code valable 15 minutes (#2, PR #15).
- L'étudiant marque sa présence avec le code : 201, 400, 403, 409 et 410 (#3, PR #16).
- L'étudiant dépose le lien de son exercice (#4, PR #17).
- Un relecteur est attribué au hasard parmi les présents, jamais l'auteur (#5, PR #18).
- Le relecteur rend une note entière de 0 à 20 et un commentaire, de manière définitive (#6, PR #19).
- Tableau de suivi du formateur (#7, PR #20).

## [analyse] — 2026-09-25 — jalon `[JALON] analyse` (`4d5cdf5`)

- Cahier des charges (10 sections), diagrammes D1 à D4 en Mermaid, contrat d'API complété, backlog de 13 issues. Aucun code avant ce jalon.
