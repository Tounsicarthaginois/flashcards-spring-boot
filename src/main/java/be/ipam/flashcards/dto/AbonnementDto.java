package be.ipam.flashcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object pour Abonnement")
public class AbonnementDto {

    @Schema(description = "ID de l'abonnement", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "ID de l'utilisateur", accessMode = Schema.AccessMode.READ_ONLY)
    private Long utilisateurId;

    @Schema(description = "ID du deck", example = "1", required = true)
    private Long deckId;

    @Schema(description = "Nom du deck", accessMode = Schema.AccessMode.READ_ONLY)
    private String deckNom;

    @Schema(description = "Nombre de nouveaux mots par jour", example = "10")
    private Integer nouveauxMotsParJour;

    @Schema(description = "Date d'abonnement", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime dateAbonnement;
}
