package be.ipam.flashcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête de connexion")
public class LoginRequest {

    @Schema(description = "Email", example = "user@example.com", required = true)
    private String email;

    @Schema(description = "Mot de passe", example = "password123", required = true)
    private String password;
}