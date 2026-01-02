package be.ipam.flashcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête pour enregistrer une révision")
public class RevisionRequest {

    @Schema(description = "ID de la flashcard", example = "1", required = true)
    private Long flashcardId;

    @Schema(description = "Révision réussie ou non", example = "true", required = true)
    private Boolean reussi;
}
