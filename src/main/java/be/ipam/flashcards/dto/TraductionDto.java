package be.ipam.flashcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object pour Traduction")
public class TraductionDto {

    @Schema(description = "ID de la traduction", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Texte traduit", example = "Hello", required = true)
    private String texte;

    @Schema(description = "ID de la langue de traduction", example = "1", required = true)
    private Long langueId;

    @Schema(description = "Nom de la langue", example = "Français", accessMode = Schema.AccessMode.READ_ONLY)
    private String langueNom;

    @Schema(description = "Liste des exemples pour cette traduction")
    private List<ExempleDto> exemples = new ArrayList<>();
}
