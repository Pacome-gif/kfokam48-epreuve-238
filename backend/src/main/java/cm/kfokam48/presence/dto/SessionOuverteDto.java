package cm.kfokam48.presence.dto;

import java.time.Instant;

/** Réponse imposée de POST /api/sessions. */
public record SessionOuverteDto(Long id, String code, Instant ouvertureAt, Instant expirationAt) {
}
