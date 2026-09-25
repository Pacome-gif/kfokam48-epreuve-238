package cm.kfokam48.presence.dto;

import cm.kfokam48.presence.domain.Presence;
import cm.kfokam48.presence.domain.SourcePresence;

/** Réponse imposée de POST /api/presences. */
public record PresenceDto(Long id, Long sessionId, Long etudiantId, SourcePresence source) {

    public static PresenceDto de(Presence p) {
        return new PresenceDto(p.getId(), p.getSession().getId(), p.getEtudiant().getId(), p.getSource());
    }
}
