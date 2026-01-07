package be.ipam.flashcards.controllers;

import be.ipam.flashcards.dto.FlashcardDto;
import be.ipam.flashcards.exception.ErrorResponse;
import be.ipam.flashcards.services.FlashcardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController  // Controller REST
@RequestMapping("/api/flashcards")  // Base URL
@Tag(name = "Flashcards", description = "API de gestion des Flashcards")
public class FlashcardController {

    private final FlashcardService flashcardService;

    public FlashcardController(FlashcardService flashcardService) {  // Injection du service
        this.flashcardService = flashcardService;
    }

    @Operation(summary = "Liste toutes les flashcards d'un deck")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    })
    @GetMapping("/deck/{deckId}")  // GET /api/flashcards/deck/5 - Toutes les flashcards du deck 5
    public ResponseEntity<List<FlashcardDto>> getFlashcardsByDeckId(@PathVariable Long deckId) {  // deckId de l'URL
        List<FlashcardDto> flashcards = flashcardService.getFlashcardsByDeckId(deckId);  // Cherche en DB
        return ResponseEntity.ok(flashcards);  // 200 + liste
    }

    @Operation(summary = "Récupère une flashcard par ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Flashcard trouvée"),
            @ApiResponse(responseCode = "404", description = "Flashcard non trouvée",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")  // GET /api/flashcards/10 - Récupère la flashcard 10
    public ResponseEntity<FlashcardDto> getFlashcardById(@PathVariable Long id) {
        FlashcardDto flashcard = flashcardService.getFlashcardById(id);
        return ResponseEntity.ok(flashcard);  // 200 + flashcard complète (avec traductions et exemples)
    }

    @Operation(summary = "Crée une flashcard complète avec traductions et exemples")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Flashcard créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping  // POST /api/flashcards - Crée une flashcard complète
    public ResponseEntity<FlashcardDto> createFlashcard(@RequestBody FlashcardDto flashcardDto) {  // Reçoit JSON avec question + traductions + exemples
        FlashcardDto createdFlashcard = flashcardService.createFlashcard(flashcardDto);  // Crée flashcard + traductions + exemples en cascade
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFlashcard);  // 201 Created
    }

    @Operation(summary = "Supprime une flashcard")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Flashcard supprimée"),
            @ApiResponse(responseCode = "404", description = "Flashcard non trouvée",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")  // DELETE /api/flashcards/10 - Supprime la flashcard 10
    public ResponseEntity<Void> deleteFlashcard(@PathVariable Long id) {
        flashcardService.deleteFlashcard(id);  // Supprime aussi traductions et exemples (cascade)
        return ResponseEntity.noContent().build();  // 204 No Content
    }
}

// Note : Le FlashcardDto contient la structure complète (traductions imbriquées, exemples imbriqués)