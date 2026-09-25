package cm.kfokam48.presence.dto;

import cm.kfokam48.presence.domain.Relecture;

/** Vue du relecteur : le lien à relire, sans le nom de l'auteur. */
public record RelectureAssigneeDto(Long id, Long exerciceId, String sessionTitre, String lien, boolean rendue,
        Integer note, String commentaire) {

    public static RelectureAssigneeDto de(Relecture r) {
        return new RelectureAssigneeDto(r.getId(), r.getExercice().getId(), r.getExercice().getSession().getTitre(),
                r.getExercice().getLien(), r.estRendue(), r.getNote(), r.getCommentaire());
    }
}
