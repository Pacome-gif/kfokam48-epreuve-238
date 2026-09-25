package cm.kfokam48.presence.dto;

import java.util.List;

import cm.kfokam48.presence.domain.StatutExercice;

/** Vue de l'auteur (EF12) : note retenue et commentaires, jamais l'identité d'un relecteur (RG16). */
public record ExerciceEtudiantDto(Long id, Long sessionId, String sessionTitre, String lien, StatutExercice statut,
        Double note, boolean noteProvisoire, int relecturesRendues, int relecturesAttendues,
        List<String> commentaires) {
}
