# D4 — États-transitions d'un exercice

**Version 2 (étape 3)** : deux relecteurs par exercice, note provisoire (RG6, RG20, RG21). Le remplacement du lien (RG11) est sorti du périmètre.

Valeurs de la colonne `exercice.statut` (voir D2). `n` = relectures assignées, `r` = relectures rendues, `requis` = `exercice.relecteurs_requis` (2).

```mermaid
stateDiagram-v2
    [*] --> DEPOSE : POST /api/exercices (EF3)<br/>[séance non clôturée, RG10]

    DEPOSE --> EN_ATTENTE_RELECTURE : 1 ou 2 relecteurs tirés (EF4, RG5-RG7)<br/>[au dépôt ou à la présence suivante, H1/H12]

    state EN_ATTENTE_RELECTURE {
        [*] --> SansNote
        SansNote --> NoteProvisoire : 1re relecture rendue<br/>[r < requis] note retenue = cette note (RG21)
        NoteProvisoire --> NoteProvisoire : second relecteur assigné (H12)
        SansNote --> SansNote : second relecteur assigné (H12)
    }

    EN_ATTENTE_RELECTURE --> RELU : dernière relecture rendue<br/>[r = requis] note retenue = moyenne (RG20, RG21)

    RELU --> [*]

    note right of DEPOSE
        Aucun présent éligible :
        l'exercice attend (tableau : « en attente », RG17)
    end note
    note right of RELU
        Définitif (RG9, Q15) :
        nouvel envoi → 409 RELECTURE_DEJA_RENDUE
    end note
```

`SansNote` et `NoteProvisoire` ne sont pas stockés : ils se déduisent de `r` et sont exposés par l'API via `noteProvisoire` (EF12) et `moyenneProvisoire` (EF6).
