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

/**
 * Controller pour les Flashcards
 */
@RestController
@RequestMapping("/api/flashcards")
@Tag(name = "Flashcards", description = "API de gestion des Flashcards")
public class FlashcardController {

    private final FlashcardService flashcardService;

    public FlashcardController(FlashcardService flashcardService) {
        this.flashcardService = flashcardService;
    }

    @Operation(summary = "Liste toutes les flashcards d'un deck")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    })
    @GetMapping("/deck/{deckId}")
    public ResponseEntity<List<FlashcardDto>> getFlashcardsByDeckId(@PathVariable Long deckId) {
        List<FlashcardDto> flashcards = flashcardService.getFlashcardsByDeckId(deckId);
        return ResponseEntity.ok(flashcards);
    }

    @Operation(summary = "Récupère une flashcard par ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Flashcard trouvée"),
            @ApiResponse(responseCode = "404", description = "Flashcard non trouvée",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<FlashcardDto> getFlashcardById(@PathVariable Long id) {
        FlashcardDto flashcard = flashcardService.getFlashcardById(id);
        return ResponseEntity.ok(flashcard);
    }

    @Operation(summary = "Crée une flashcard complète avec traductions et exemples")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Flashcard créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<FlashcardDto> createFlashcard(@RequestBody FlashcardDto flashcardDto) {
        FlashcardDto createdFlashcard = flashcardService.createFlashcard(flashcardDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFlashcard);
    }

    @Operation(summary = "Supprime une flashcard")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Flashcard supprimée"),
            @ApiResponse(responseCode = "404", description = "Flashcard non trouvée",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlashcard(@PathVariable Long id) {
        flashcardService.deleteFlashcard(id);
        return ResponseEntity.noContent().build();
    }
}