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
    private Long id;  // Généré par la DB

    @Schema(description = "Question/mot dans la langue étudiée", example = "Hello", required = true)
    private String question;  // Le mot à apprendre (ex: "Apple", "Hello")

    @Schema(description = "ID du deck", example = "1", required = true)
    private Long deckId;  // À quel deck appartient cette flashcard

    @Schema(description = "Date de création", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;  // Rempli automatiquement

    @Schema(description = "Liste des traductions avec exemples")
    private List<TraductionDto> traductions = new ArrayList<>();  // Liste vide par défaut, peut contenir plusieurs traductions
}

// DTO pour créer/lire une flashcard complète
// Structure imbriquée : FlashcardDto contient une liste de TraductionDto
// Chaque TraductionDto contient elle-même une liste d'ExempleDto
// Permet de créer toute la structure en une seule requête POST