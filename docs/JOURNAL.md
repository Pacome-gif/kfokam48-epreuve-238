# Journal — KF48-238

## Étape 1 — Analyse et conception (≈ 10h05 → 10h45)

**Fait** : cahier des charges (14 EF, 8 ENF, 18 RG, 10 hypothèses/contradictions tranchées), 4 diagrammes Mermaid (D1 cas d'utilisation, D2 modèle de données, D3 séquence présence, D4 états d'un exercice — bonus), contrat OpenAPI complété (5 opérations imposées + 9), 13 issues (7 Must, 5 Should, 1 Could) reliées aux EF/RG, puis `[JALON] analyse`.

**Bloqué** : ~15 min au démarrage : mon dossier personnel entier était déjà un dépôt Git et le dossier EPREUVE/ était vide ; j'ai créé un dépôt séparé et recréé le contrat depuis l'Annexe B. ~10 min pour installer et connecter `gh`. ~10 min sur Q10/Q15, tranchée en faveur de Q15 parce que le contrat impose `409 relecture déjà rendue`.

**IA** : Claude m'a proposé le cahier des charges, les diagrammes, le contrat et le backlog. Vérifié : chaque RG renvoie à une Qx de CLIENT.md, chaque code HTTP de D3 existe dans `contrat.yaml` (relu à la main), le YAML est parsé sans erreur (`python3 -c "import yaml…"`), et chaque titre d'issue décrit un résultat que le client comprendrait. J'ai relu la section 7 pour être capable de défendre chaque décision.
