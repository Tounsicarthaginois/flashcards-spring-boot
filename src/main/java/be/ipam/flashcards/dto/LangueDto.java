package be.ipam.flashcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object pour Langue")
public class LangueDto {

    @Schema(description = "ID de la langue", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Nom de la langue", example = "Français", required = true)
    private String nom;

    @Schema(description = "Code de la langue", example = "fr", required = true)
    private String code;
}
