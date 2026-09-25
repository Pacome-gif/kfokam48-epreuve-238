package cm.kfokam48.presence.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SessionCreationDto(
        @NotBlank(message = "est obligatoire") @Size(max = 200) String titre,
        @NotNull(message = "est obligatoire") Long promotionId) {
}
