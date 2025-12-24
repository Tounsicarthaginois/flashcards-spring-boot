package be.ipam.flashcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object pour Utilisateur")
public class UtilisateurDto {

    @Schema(description = "ID de l'utilisateur", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Email", example = "user@example.com", required = true)
    private String email;

    @Schema(description = "Nom", example = "Dupont", required = true)
    private String nom;

    @Schema(description = "Prénom", example = "Jean", required = true)
    private String prenom;
}