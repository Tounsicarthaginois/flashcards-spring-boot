package be.ipam.flashcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object pour Langue")
public class LangueDto {

    @Schema(description = "ID de la langue", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;  // Généré par la DB

    @Schema(description = "Nom de la langue", example = "Français", required = true)
    private String nom;  // "Français", "Anglais", "Espagnol"...

    @Schema(description = "Code de la langue", example = "fr", required = true)
    private String code;  // "fr", "en", "es" (code ISO 639-1)
}

// DTO simple pour les langues disponibles dans l'application
// Seuls les GESTIONNAIRE peuvent créer/modifier/supprimer des langues
// Les USER peuvent juste lire la liste