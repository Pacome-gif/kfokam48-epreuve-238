package cm.kfokam48.presence.dto;

import jakarta.validation.constraints.NotNull;

public record PresenceManuelleDto(@NotNull(message = "est obligatoire") Long etudiantId) {
}
