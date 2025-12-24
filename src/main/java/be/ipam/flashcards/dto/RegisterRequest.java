package be.ipam.flashcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête d'inscription")
public class RegisterRequest {

    @Schema(description = "Email de l'utilisateur", example = "user@example.com", required = true)
    private String email;

    @Schema(description = "Mot de passe", example = "password123", required = true)
    private String password;

    @Schema(description = "Nom", example = "Dupont", required = true)
    private String nom;

    @Schema(description = "Prénom", example = "Jean", required = true)
    private String prenom;
}
