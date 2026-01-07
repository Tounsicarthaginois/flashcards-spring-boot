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
    private Long id;  // Généré par la DB

    @Schema(description = "ID de l'utilisateur", accessMode = Schema.AccessMode.READ_ONLY)
    private Long utilisateurId;  // Récupéré automatiquement via JWT

    @Schema(description = "ID de la flashcard", example = "1", required = true)
    private Long flashcardId;  // Quelle flashcard est concernée

    @Schema(description = "Question de la flashcard", accessMode = Schema.AccessMode.READ_ONLY)
    private String question;  // "Apple" (pour affichage, calculé par le mapper)

    @Schema(description = "État de progression", example = "EN_COURS")
    private EtatProgression etat;  // NOUVEAU / EN_COURS / CONNU

    @Schema(description = "Niveau de connaissance (0-5)", example = "2")
    private Integer niveauConnaissance;  // 0 = nouveau, 5 = maîtrisé

    @Schema(description = "Date de prochaine révision")
    private LocalDateTime prochaineRevision;  // Calculé par l'algorithme SRS (1j, 30j, 60j...)

    @Schema(description = "Nombre de révisions réussies consécutives", example = "3")
    private Integer nbRevisionsReussies;  // Compteur remis à 0 si échec

    @Schema(description = "Dernière révision")
    private LocalDateTime derniereRevision;  // Dernière fois que l'user a révisé cette carte

    @Schema(description = "Date de création", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;  // Date de première révision
}

// DTO central du système SRS (répétition espacée)
// Table intermédiaire N-N : un utilisateur révise plusieurs flashcards
// Contient toutes les stats pour l'algorithme (état, niveau, prochaine révision...)