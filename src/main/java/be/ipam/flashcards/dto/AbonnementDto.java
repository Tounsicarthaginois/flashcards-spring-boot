package be.ipam.flashcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data  // Lombok génère getters, setters, toString, equals, hashCode automatiquement
@NoArgsConstructor  // Constructeur vide (obligatoire pour Jackson JSON)
@AllArgsConstructor  // Constructeur avec tous les paramètres
@Schema(description = "Data Transfer Object pour Abonnement")  // Description dans Swagger
public class AbonnementDto {

    @Schema(description = "ID de l'abonnement", example = "1", accessMode = Schema.AccessMode.READ_ONLY)  // READ_ONLY = généré par DB
    private Long id;

    @Schema(description = "ID de l'utilisateur", accessMode = Schema.AccessMode.READ_ONLY)  // Récupéré automatiquement via JWT
    private Long utilisateurId;

    @Schema(description = "ID du deck", example = "1", required = true)  // Obligatoire à la création
    private Long deckId;

    @Schema(description = "Nom du deck", accessMode = Schema.AccessMode.READ_ONLY)  // Pour affichage uniquement
    private String deckNom;  // Ex: "Vocabulaire TOEFL" (calculé côté service)

    @Schema(description = "Nombre de nouveaux mots par jour", example = "10")  // Paramètre d'apprentissage
    private Integer nouveauxMotsParJour;  // Par défaut = 10 (dans @PrePersist de l'entité)

    @Schema(description = "Date d'abonnement", accessMode = Schema.AccessMode.READ_ONLY)  // Rempli automatiquement
    private LocalDateTime dateAbonnement;  // Date de création de l'abonnement
}

// DTO utilisé pour s'abonner à un deck et paramétrer l'apprentissage
// Table intermédiaire N-N : un user peut s'abonner à plusieurs decks