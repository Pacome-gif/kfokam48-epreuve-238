# D2 — Modèle de données

Ce diagramme correspond **exactement** aux migrations Flyway de `backend/src/main/resources/db/migration/` : mêmes noms de tables, de colonnes et de contraintes. Toute évolution du schéma passe par une nouvelle migration **et** une mise à jour de ce fichier.

```mermaid
erDiagram
    PROMOTION ||--o{ ETUDIANT : "regroupe"
    PROMOTION ||--o{ SESSION_COURS : "a"
    SESSION_COURS ||--o{ PRESENCE : "enregistre"
    ETUDIANT ||--o{ PRESENCE : "a"
    SESSION_COURS ||--o{ EXERCICE : "reçoit"
    ETUDIANT ||--o{ EXERCICE : "dépose"
    EXERCICE ||--o| RELECTURE : "est relu par"
    ETUDIANT ||--o{ RELECTURE : "relit"
    ETUDIANT ||--o| TENTATIVE_CODE : "compte ses erreurs"

    PROMOTION {
        bigint id PK
        varchar nom UK "NOT NULL"
    }
    ETUDIANT {
        bigint id PK
        varchar nom "NOT NULL"
        bigint promotion_id FK "NOT NULL"
    }
    SESSION_COURS {
        bigint id PK
        varchar titre "NOT NULL"
        bigint promotion_id FK "NOT NULL"
        varchar code UK "6 caractères, NOT NULL"
        timestamptz ouverture_at "NOT NULL"
        timestamptz expiration_at "ouverture_at + 15 min (RG1)"
        timestamptz cloture_at "NULL tant que non clôturée"
    }
    PRESENCE {
        bigint id PK
        bigint session_id FK "NOT NULL"
        bigint etudiant_id FK "NOT NULL"
        varchar source "ETUDIANT | FORMATEUR (RG15)"
        timestamptz created_at "NOT NULL"
    }
    EXERCICE {
        bigint id PK
        bigint session_id FK "NOT NULL"
        bigint etudiant_id FK "NOT NULL"
        varchar lien "URL http(s) (RG13)"
        varchar statut "DEPOSE | EN_ATTENTE_RELECTURE | RELU"
        timestamptz depose_at "NOT NULL"
    }
    RELECTURE {
        bigint id PK
        bigint exercice_id FK,UK "un seul relecteur (RG6)"
        bigint relecteur_id FK "≠ auteur (RG5)"
        int note "NULL tant que non rendue, 0..20 (RG8)"
        varchar commentaire "NULL tant que non rendue"
        timestamptz assignee_at "NOT NULL"
        timestamptz rendue_at "NULL tant que non rendue (RG9)"
    }
    TENTATIVE_CODE {
        bigint etudiant_id PK,FK
        int echecs "codes inconnus consécutifs"
        timestamptz bloque_jusqua "NULL si non bloqué (RG4)"
    }
```

## Contraintes portées par la base

| Contrainte | Règle |
|---|---|
| `UNIQUE (session_id, etudiant_id)` sur `presence` | RG2 — une seule présence par séance |
| `UNIQUE (session_id, etudiant_id)` sur `exercice` | RG12 — un seul dépôt par séance |
| `UNIQUE (exercice_id)` sur `relecture` | RG6 — un seul relecteur |
| `CHECK (note BETWEEN 0 AND 20)` sur `relecture` | RG8 |
| `CHECK (source IN ('ETUDIANT','FORMATEUR'))` | RG15 |
| `CHECK (statut IN ('DEPOSE','EN_ATTENTE_RELECTURE','RELU'))` | D4 |

La règle « relecteur ≠ auteur » (RG5) est vérifiée dans le service, parce qu'elle porte sur deux tables.
