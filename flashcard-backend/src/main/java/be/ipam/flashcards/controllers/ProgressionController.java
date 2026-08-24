package be.ipam.flashcards.controllers;

import be.ipam.flashcards.dto.FlashcardDto;
import be.ipam.flashcards.dto.ProgressionUtilisateurDto;
import be.ipam.flashcards.dto.RevisionRequest;
import be.ipam.flashcards.exception.ErrorResponse;
import be.ipam.flashcards.services.ProgressionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/progressions")
@Tag(name = "Progressions", description = "API de gestion des progressions SRS")
public class ProgressionController {

    private final ProgressionService progressionService;

    public ProgressionController(ProgressionService progressionService) {
        this.progressionService = progressionService;
    }

    @Operation(summary = "Enregistre une révision (succès ou échec)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Révision enregistrée"),
            @ApiResponse(responseCode = "404", description = "Flashcard non trouvée",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/revision")
    // POST /api/progressions/revision → reçoit flashcardId + reussi (true/false)
    // Calcule la prochaine date : 1j si raté, sinon 1j / 7j / 30j selon le niveau atteint
    public ResponseEntity<ProgressionUtilisateurDto> enregistrerRevision(@RequestBody RevisionRequest request) {
        return ResponseEntity.ok(progressionService.enregistrerRevision(request));
    }

    @Operation(summary = "Récupère les flashcards à réviser aujourd'hui pour un deck")
    @GetMapping("/a-reviser/{deckId}")
    // GET /api/progressions/a-reviser/{deckId}
    // Retourne : nouvelles cartes + cartes dont la date est passée
    // Exclut : cartes dont prochaineRevision est dans le futur
    public ResponseEntity<List<FlashcardDto>> getCardsAReviserParDeck(@PathVariable Long deckId) {
        return ResponseEntity.ok(progressionService.getCardsAReviserParDeck(deckId));
    }

    @Operation(summary = "Récupère les progressions SRS de l'utilisateur pour un deck")
    @GetMapping("/deck/{deckId}")
    // GET /api/progressions/deck/{deckId}
    // Retourne les progressions existantes (cartes déjà révisées au moins 1 fois)
    // Les cartes sans progression = nouvelles (pas dans la liste)
    // Utilisé pour afficher les badges SRS sur la page flashcards
    public ResponseEntity<List<ProgressionUtilisateurDto>> getProgressionsByDeck(@PathVariable Long deckId) {
        return ResponseEntity.ok(progressionService.getProgressionsByDeck(deckId));
    }

    @Operation(summary = "Récupère les flashcards à réviser aujourd'hui (tous decks)")
    @GetMapping("/a-reviser")
    public ResponseEntity<List<ProgressionUtilisateurDto>> getFlashcardsAReviser() {
        return ResponseEntity.ok(progressionService.getFlashcardsAReviser());
    }

    @Operation(summary = "Récupère toutes mes progressions")
    @GetMapping("/mes-progressions")
    public ResponseEntity<List<ProgressionUtilisateurDto>> getMesProgressions() {
        return ResponseEntity.ok(progressionService.getMesProgressions());
    }
}