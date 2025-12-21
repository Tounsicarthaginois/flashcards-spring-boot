package be.ipam.flashcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour les Flashcards
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object pour Flashcard")
public class FlashcardDto {

    @Schema(description = "ID de la flashcard", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Question", example = "What is OOP?", required = true)
    private String question;

    @Schema(description = "Réponse", example = "Object-Oriented Programming", required = true)
    private String answer;

    @Schema(description = "ID du deck", example = "5")
    private Long deckId;

    @Schema(description = "Date de création", example = "2024-12-21T19:00:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;
}