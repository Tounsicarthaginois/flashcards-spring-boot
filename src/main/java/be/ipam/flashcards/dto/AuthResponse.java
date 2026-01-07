package be.ipam.flashcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data  // Getters, setters, toString...
@NoArgsConstructor  // Constructeur vide
@AllArgsConstructor  // Constructeur avec tous les champs
@Schema(description = "Réponse d'authentification")
public class AuthResponse {

    @Schema(description = "Token JWT", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;  // Le token JWT à inclure dans les requêtes suivantes

    @Schema(description = "Type de token", example = "Bearer")
    private String type = "Bearer";  // Toujours "Bearer" pour JWT (Authorization: Bearer <token>)

    @Schema(description = "Email de l'utilisateur", example = "user@example.com")
    private String email;  // Email de l'utilisateur connecté

    @Schema(description = "Rôle de l'utilisateur", example = "USER")
    private String role;  // USER ou GESTIONNAIRE

    public AuthResponse(String token, String email, String role) {  // Constructeur custom sans type
        this.token = token;  // (type est automatiquement "Bearer")
        this.email = email;
        this.role = role;
    }
}

// Renvoyé par POST /api/auth/register et POST /api/auth/login
// Le client doit copier le token et l'inclure dans toutes les requêtes suivantes