package be.ipam.flashcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object pour Exemple")
public class ExempleDto {

    @Schema(description = "ID de l'exemple", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Phrase dans la langue étudiée", example = "Hello, how are you?", required = true)
    private String phraseOriginal;

    @Schema(description = "Traduction de la phrase", example = "Bonjour, comment allez-vous ?", required = true)
    private String phraseTraduite;
}
