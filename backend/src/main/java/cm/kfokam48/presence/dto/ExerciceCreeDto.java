package cm.kfokam48.presence.dto;

import cm.kfokam48.presence.domain.Exercice;
import cm.kfokam48.presence.domain.StatutExercice;

/** Réponse imposée de POST /api/exercices. */
public record ExerciceCreeDto(Long id, StatutExercice statut) {

    public static ExerciceCreeDto de(Exercice e) {
        return new ExerciceCreeDto(e.getId(), e.getStatut());
    }
}
