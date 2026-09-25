# D1 — Cas d'utilisation

Mermaid n'a pas de type « use case » natif : les acteurs sont représentés par des nœuds ronds, les cas d'utilisation par des nœuds arrondis dans le cadre du système. Les références EFx renvoient au cahier des charges.

```mermaid
flowchart LR
    F((Formateur))
    E((Étudiant))
    R((Relecteur))
    SYS((Système))

    subgraph APP["Application KFOKAM48 — Présence & Relecture"]
        UC1([Ouvrir une séance et obtenir un code — EF1])
        UC9([Ajouter une présence à la main — EF9])
        UC10([Clôturer une séance — EF10])
        UC6([Consulter le tableau de la promotion — EF6])

        UC7([Choisir son nom dans la liste — EF7])
        UC2([Marquer sa présence avec le code — EF2])
        UC3([Déposer le lien de son exercice — EF3])
        UC11([Remplacer le lien de son exercice — EF11])
        UC12([Consulter la note reçue — EF12])

        UC8([Voir les relectures à faire — EF8])
        UC5([Rendre une note et un commentaire — EF5])

        UC4([Assigner un relecteur au hasard — EF4])
        UC13([Bloquer après 5 codes erronés — EF13])
    end

    F --- UC1
    F --- UC9
    F --- UC10
    F --- UC6

    E --- UC7
    E --- UC2
    E --- UC3
    E --- UC11
    E --- UC12

    R --- UC8
    R --- UC5

    SYS --- UC4
    SYS --- UC13

    UC2 -. "«include»" .-> UC7
    UC3 -. "«include»" .-> UC7
    UC3 -. "«include»" .-> UC4
    UC2 -. "«extend» 5 erreurs" .-> UC13
    UC5 -. "«include»" .-> UC8
```

Note : le **Relecteur** n'est pas un compte à part. C'est un Étudiant présent à la séance que le système a désigné (RG7).
