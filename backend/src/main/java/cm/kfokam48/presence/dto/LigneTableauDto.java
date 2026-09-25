package cm.kfokam48.presence.dto;

/**
 * Ligne du tableau formateur (contrat : GET /api/tableau).
 * exercicesEnAttente (H9, Q11) et moyenneProvisoire (RG14 v2) sont des champs ajoutés au contrat imposé.
 */
public record LigneTableauDto(Long etudiantId, String nom, long presences, long exercicesDeposes, Double moyenne,
        boolean moyenneProvisoire, long relecturesEnAttente, long exercicesEnAttente) {
}
