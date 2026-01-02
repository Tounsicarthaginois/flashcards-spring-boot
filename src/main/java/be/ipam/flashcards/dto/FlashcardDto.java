package be.ipam.flashcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object pour Flashcard")
public class FlashcardDto {

    @Schema(description = "ID de la flashcard", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Question/mot dans la langue étudiée", example = "Hello", required = true)
    private String question;

    @Schema(description = "ID du deck", example = "1", required = true)
    private Long deckId;

    @Schema(description = "Date de création", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Schema(description = "Liste des traductions avec exemples")
    private List<TraductionDto> traductions = new ArrayList<>();
}