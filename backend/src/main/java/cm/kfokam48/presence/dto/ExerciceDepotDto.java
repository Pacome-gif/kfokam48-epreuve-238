package cm.kfokam48.presence.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ExerciceDepotDto(
        @NotNull(message = "est obligatoire") Long sessionId,
        @NotNull(message = "est obligatoire") Long etudiantId,
        @NotBlank(message = "est obligatoire") String lien) {
}
