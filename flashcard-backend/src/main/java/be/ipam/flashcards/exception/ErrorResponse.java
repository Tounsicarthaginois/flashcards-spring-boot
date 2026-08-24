package be.ipam.flashcards.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Objet de réponse d'erreur
 *
 * Sera retourné au client en JSON quand une erreur se produit
 * Exemple de réponse :
 * {
 *   "status": 404,
 *   "message": "Deck not found with id : '5'",
 *   "timestamp": "2024-12-21T19:00:00"
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Objet de réponse en cas d'erreur")
public class ErrorResponse {

    @Schema(description = "Code de statut HTTP", example = "404")
    private int status;

    @Schema(description = "Message d'erreur", example = "Deck not found with id : '5'")
    private String message;

    @Schema(description = "Date et heure de l'erreur", example = "2024-12-21T19:00:00")
    private LocalDateTime timestamp;
}
