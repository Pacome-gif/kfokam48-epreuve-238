package cm.kfokam48.presence.dto;

import java.time.Instant;

import cm.kfokam48.presence.domain.Presence;
import cm.kfokam48.presence.domain.SourcePresence;

/** Présence d'une séance, avec sa source visible pour le formateur (Q14). */
public record PresenceDetailDto(Long id, Long sessionId, Long etudiantId, SourcePresence source, String nom,
        Instant createdAt) {

    public static PresenceDetailDto de(Presence p) {
        return new PresenceDetailDto(p.getId(), p.getSession().getId(), p.getEtudiant().getId(), p.getSource(),
                p.getEtudiant().getNom(), p.getCreatedAt());
    }
}
