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
    private Long id;

    @Schema(description = "Nom du deck", example = "Vocabulaire TOEFL", required = true)
    private String name;

    @Schema(description = "Description", example = "Mots essentiels pour le TOEFL")
    private String description;

    @Schema(description = "Type de liste", example = "PRIVEE", required = true)
    private TypeListe type;

    @Schema(description = "ID de la langue étudiée", example = "1", required = true)
    private Long langueId;

    @Schema(description = "Nom de la langue étudiée", example = "Anglais", accessMode = Schema.AccessMode.READ_ONLY)
    private String langueNom;

    @Schema(description = "ID du créateur", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long userId;

    @Schema(description = "ID du validateur (si liste publique)", example = "2")
    private Long validateurId;

    @Schema(description = "Date de création", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Schema(description = "Nombre de flashcards", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer flashcardCount;
}