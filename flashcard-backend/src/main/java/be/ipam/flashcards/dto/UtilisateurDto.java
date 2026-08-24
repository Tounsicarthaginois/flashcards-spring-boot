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
    private Long id;  // Généré par la DB

    @Schema(description = "Email", example = "user@example.com", required = true)
    private String email;  // Email unique (login)

    @Schema(description = "Nom", example = "Dupont", required = true)
    private String nom;  // Nom de famille

    @Schema(description = "Prénom", example = "Jean", required = true)
    private String prenom;  // Prénom
}

// DTO pour renvoyer les infos d'un utilisateur dans l'API
// IMPORTANT : Pas de champ "password" ici ! (sécurité)
// Le mapper Entity → DTO exclut automatiquement le password
// Différence principale avec l'entité Utilisateur : absence du password et du role