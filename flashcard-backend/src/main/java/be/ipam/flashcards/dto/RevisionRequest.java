package be.ipam.flashcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête pour enregistrer une révision")
public class RevisionRequest {

    @Schema(description = "ID de la flashcard", example = "1", required = true)
    private Long flashcardId;  // Quelle flashcard l'utilisateur vient de réviser

    @Schema(description = "Révision réussie ou non", example = "true", required = true)
    private Boolean reussi;  // true = succès, false = échec
}

// DTO envoyé par le client après avoir révisé une flashcard
// ProgressionService utilise ce boolean pour calculer la prochaine révision :
// - Si reussi=true : +1j (1ère fois), +7j (2ème fois), +30j (3ème fois et plus, état CONNU)
// - Si reussi=false : retour à +1j (reset du compteur)
