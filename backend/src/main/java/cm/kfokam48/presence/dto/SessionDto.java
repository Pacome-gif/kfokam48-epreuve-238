package cm.kfokam48.presence.dto;

import java.time.Instant;

import cm.kfokam48.presence.domain.SessionCours;

public record SessionDto(Long id, String titre, Long promotionId, String code, Instant ouvertureAt,
        Instant expirationAt, Instant clotureAt) {

    public static SessionDto de(SessionCours s) {
        return new SessionDto(s.getId(), s.getTitre(), s.getPromotion().getId(), s.getCode(), s.getOuvertureAt(),
                s.getExpirationAt(), s.getClotureAt());
    }
}
