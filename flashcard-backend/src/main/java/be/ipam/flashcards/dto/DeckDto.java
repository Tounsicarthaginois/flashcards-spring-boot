package be.ipam.flashcards.dto;

import be.ipam.flashcards.enums.TypeListe;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object pour Deck")
public class DeckDto {

    @Schema(description = "ID du deck", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;  // Généré par la DB

    @Schema(description = "Nom du deck", example = "Vocabulaire TOEFL", required = true)
    private String name;  // Obligatoire à la création

    @Schema(description = "Description", example = "Mots essentiels pour le TOEFL")
    private String description;  // Optionnel

    @Schema(description = "Type de liste", example = "PRIVEE", required = true)
    private TypeListe type;  // OFFICIELLE / PRIVEE / PUBLIQUE

    @Schema(description = "ID de la langue étudiée", example = "1", required = true)
    private Long langueId;  // Obligatoire (référence à la table langues)

    @Schema(description = "Nom de la langue étudiée", example = "Anglais", accessMode = Schema.AccessMode.READ_ONLY)
    private String langueNom;  // Pour affichage (calculé par le mapper depuis langue.nom)

    @Schema(description = "ID du créateur", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long userId;  // Récupéré automatiquement via JWT (utilisateur connecté)

    @Schema(description = "ID du validateur (si liste publique)", example = "2")
    private Long validateurId;  // Null si pas encore validé, rempli par gestionnaire

    @Schema(description = "Date de création", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;  // Rempli par @PrePersist

    @Schema(description = "Nombre de flashcards", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer flashcardCount;  // Calculé dynamiquement (flashcards.size())
}

// DTO pour créer/modifier des decks (listes de flashcards)
// Ne contient pas l'objet Langue complet, juste son ID et son nom (plus simple pour le client)