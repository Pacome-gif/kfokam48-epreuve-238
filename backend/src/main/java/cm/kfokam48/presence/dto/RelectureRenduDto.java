package cm.kfokam48.presence.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Corps imposé de POST /api/relectures/{id}. La note est lue en décimal pour pouvoir refuser
 * explicitement 12.5 avec NOTE_INVALIDE (RG8) au lieu d'une erreur de format.
 */
public record RelectureRenduDto(
        @NotNull(message = "est obligatoire") BigDecimal note,
        @NotBlank(message = "est obligatoire") @Size(max = 2000) String commentaire) {
}
