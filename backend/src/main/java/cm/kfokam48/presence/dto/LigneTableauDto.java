package cm.kfokam48.presence.dto;

/**
 * Ligne du tableau formateur (contrat : GET /api/tableau).
 * exercicesEnAttente est un champ ajouté (H9) pour rendre visible Q11.
 */
public record LigneTableauDto(Long etudiantId, String nom, long presences, long exercicesDeposes, Double moyenne,
        long relecturesEnAttente, long exercicesEnAttente) {
}
