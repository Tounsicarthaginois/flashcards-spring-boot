package be.ipam.flashcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Réponse d'authentification")
public class AuthResponse {

    @Schema(description = "Token JWT")
    private String token;

    @Schema(description = "Type de token", example = "Bearer")
    private String type = "Bearer";

    @Schema(description = "Email de l'utilisateur")
    private String email;

    @Schema(description = "Rôle de l'utilisateur", example = "USER")
    private String role;

    @Schema(description = "ID de l'utilisateur connecté")
    private Long userId;  // Ajouté pour permettre de vérifier si l'user est le créateur d'un deck

    // Constructeur utilisé dans AuthService (login + register)
    public AuthResponse(String token, String email, String role, Long userId) {
        this.token = token;
        this.email = email;
        this.role = role;
        this.userId = userId;
    }
}