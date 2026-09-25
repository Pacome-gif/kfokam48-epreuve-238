# D3 — Séquence : marquer sa présence

Les codes HTTP et les codes d'erreur sont ceux de `api/contrat.yaml`. L'ordre des vérifications dans le service est celui de ce diagramme (`PresenceService.marquer`).

**Version 2 (étape 3)** :
- depuis le correctif du bug #21, l'attribution des exercices en attente a lieu **après** la validation de la présence, dans sa propre transaction ; un conflit avec une présence simultanée ne peut plus annuler la présence (H14) ;
- le blocage après 5 codes erronés (RG4, `429`) est sorti du périmètre et n'apparaît plus.

```mermaid
sequenceDiagram
    autonumber
    actor E as Étudiant
    participant F as Front (Next.js)
    participant API as PresenceController
    participant G as GestionErreurs (@RestControllerAdvice)
    participant S as PresenceService
    participant SR as SessionRepository
    participant PR as PresenceRepository
    participant A as AssignationService

    E->>F: choisit son nom (EF7) et saisit le code
    F->>API: POST /api/presences { code, etudiantId }
    API->>API: validation @Valid (champs obligatoires)
    alt champ manquant
        API->>G: MethodArgumentNotValidException
        G-->>F: 400 { code: "VALIDATION", message }
    end
    API->>S: marquer(code, etudiantId)
    S->>SR: findByCode(code)
    alt code inconnu
        S-->>G: MetierException(CODE_INCONNU)
        G-->>F: 400 { code: "CODE_INCONNU" }
    else séance clôturée (RG3)
        S-->>G: MetierException(SESSION_CLOTUREE)
        G-->>F: 410 { code: "SESSION_CLOTUREE" }
    else code expiré (RG1 : maintenant > expirationAt)
        S-->>G: MetierException(CODE_EXPIRE)
        G-->>F: 410 { code: "CODE_EXPIRE" }
    else étudiant déjà présent (RG2)
        S->>PR: existsBySessionIdAndEtudiantId
        PR-->>S: true
        S-->>G: MetierException(DEJA_PRESENT)
        G-->>F: 409 { code: "DEJA_PRESENT" }
    else cas nominal
        S->>PR: saveAndFlush(Presence source=ETUDIANT)
        S-->>API: PresenceDto
        Note over S,PR: transaction validée : la présence est enregistrée
        S->>A: afterCommit → assignerEnAttente(session) (H1, H12)
        Note over A: transaction séparée (REQUIRES_NEW),<br/>exercice verrouillé ; conflit ignoré (bug #21)
        API-->>F: 201 { id, sessionId, etudiantId, source: "ETUDIANT" }
        F-->>E: « Présence enregistrée »
    end
```

`ETUDIANT_INCONNU` (400) est vérifié avant la recherche du code, et `HORS_PROMOTION` (403, H10) juste avant le contrôle « déjà présent ». Ils ne sont pas dessinés ici pour garder le diagramme lisible.
