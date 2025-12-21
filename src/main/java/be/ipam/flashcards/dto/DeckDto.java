package be.ipam.flashcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO pour les Decks
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object pour Deck")
public class DeckDto {

    @Schema(description = "ID du deck", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Nom du deck", example = "Vocabulaire Anglais", required = true)
    private String name;

    @Schema(description = "Description du deck", example = "Mots pour le TOEFL", nullable = true)
    private String description;

    @Schema(description = "ID de l'utilisateur propriétaire", example = "5", accessMode = Schema.AccessMode.READ_ONLY)
    private Long userId;

    @Schema(description = "Deck public ou privé", example = "false", defaultValue = "false")
    private boolean isPublic;

    @Schema(description = "Date de création", example = "2024-12-21T19:00:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Schema(description = "Nombre de flashcards", example = "15", accessMode = Schema.AccessMode.READ_ONLY)
    private int flashcardCount;

    @Schema(description = "Liste des flashcards (optionnel)", nullable = true)
    private List<FlashcardDto> flashcards;
}
