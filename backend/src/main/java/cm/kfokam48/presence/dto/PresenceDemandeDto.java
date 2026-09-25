package cm.kfokam48.presence.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PresenceDemandeDto(
        @NotBlank(message = "est obligatoire") String code,
        @NotNull(message = "est obligatoire") Long etudiantId) {
}
