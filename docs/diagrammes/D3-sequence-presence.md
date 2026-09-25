# D3 — Séquence : marquer sa présence

Les codes HTTP et les codes d'erreur sont ceux de `api/contrat.yaml`. L'ordre des vérifications dans le service est celui de ce diagramme.

```mermaid
sequenceDiagram
    autonumber
    actor E as Étudiant
    participant F as Front (Next.js)
    participant API as PresenceController
    participant S as PresenceService
    participant T as TentativeCodeRepository
    participant SR as SessionRepository
    participant PR as PresenceRepository
    participant A as AssignationService

    E->>F: choisit son nom (EF7) et saisit le code
    F->>API: POST /api/presences { code, etudiantId }
    API->>API: validation @Valid (champs obligatoires)
    alt champ manquant
        API-->>F: 400 { code: "VALIDATION", message }
    end
    API->>S: marquer(code, etudiantId)
    S->>T: bloqué jusqu'à ? (RG4)
    alt étudiant bloqué (5 erreurs < 2 min)
        S-->>API: TropDeTentativesException
        API-->>F: 429 { code: "TROP_DE_TENTATIVES" }
    end
    S->>SR: findByCode(code)
    alt code inconnu
        S->>T: echecs + 1 (bloque 2 min si 5)
        S-->>API: CodeInconnuException
        API-->>F: 400 { code: "CODE_INCONNU" }
    else séance clôturée (RG3)
        S-->>API: SessionClotureeException
        API-->>F: 410 { code: "SESSION_CLOTUREE" }
    else code expiré (RG1 : maintenant > expirationAt)
        S-->>API: CodeExpireException
        API-->>F: 410 { code: "CODE_EXPIRE" }
    else étudiant déjà présent (RG2)
        S->>PR: existsBySessionAndEtudiant
        PR-->>S: true
        S-->>API: DejaPresentException
        API-->>F: 409 { code: "DEJA_PRESENT" }
    else cas nominal
        S->>PR: save(Presence source=ETUDIANT)
        S->>T: echecs = 0
        S->>A: assignerExercicesEnAttente(session) (H1)
        S-->>API: PresenceDto
        API-->>F: 201 { id, sessionId, etudiantId, source: "ETUDIANT" }
        F-->>E: « Présence enregistrée »
    end
```

Le cas « étudiant d'une autre promotion » (`403 HORS_PROMOTION`, H10) est vérifié juste avant le contrôle « déjà présent ». Il n'est pas dessiné ici pour garder le diagramme lisible.
