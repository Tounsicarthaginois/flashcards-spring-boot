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
    private String email;  // Doit être unique (vérifié par AuthService)

    @Schema(description = "Mot de passe", example = "password123", required = true)
    private String password;  // En clair (sera crypté avec BCrypt avant insertion en DB)

    @Schema(description = "Nom", example = "Dupont", required = true)
    private String nom;  // Nom de famille

    @Schema(description = "Prénom", example = "Jean", required = true)
    private String prenom;  // Prénom
}

// DTO envoyé par le client pour s'inscrire
// AuthService crée l'utilisateur avec role USER par défaut et renvoie un token JWT