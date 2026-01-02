package be.ipam.flashcards.dto;

import be.ipam.flashcards.enums.EtatProgression;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object pour Progression Utilisateur")
public class ProgressionUtilisateurDto {

    @Schema(description = "ID de la progression", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "ID de l'utilisateur", accessMode = Schema.AccessMode.READ_ONLY)
    private Long utilisateurId;

    @Schema(description = "ID de la flashcard", example = "1", required = true)
    private Long flashcardId;

    @Schema(description = "Question de la flashcard", accessMode = Schema.AccessMode.READ_ONLY)
    private String question;

    @Schema(description = "État de progression", example = "EN_COURS")
    private EtatProgression etat;

    @Schema(description = "Niveau de connaissance (0-5)", example = "2")
    private Integer niveauConnaissance;

    @Schema(description = "Date de prochaine révision")
    private LocalDateTime prochaineRevision;

    @Schema(description = "Nombre de révisions réussies consécutives", example = "3")
    private Integer nbRevisionsReussies;

    @Schema(description = "Dernière révision")
    private LocalDateTime derniereRevision;

    @Schema(description = "Date de création", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;
}