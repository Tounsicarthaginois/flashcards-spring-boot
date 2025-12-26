package be.ipam.flashcards.controllers;

import be.ipam.flashcards.dto.DeckDto;
import be.ipam.flashcards.exception.ErrorResponse;
import be.ipam.flashcards.services.DeckService;
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
 * Controller pour les Decks
 */
@RestController
@RequestMapping("/api/decks")
@Tag(name = "Decks", description = "API de gestion des Decks")
public class DeckController {

    private final DeckService deckService;

    public DeckController(DeckService deckService) {
        this.deckService = deckService;
    }

    @Operation(summary = "Liste tous les decks de l'utilisateur")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    })
    @GetMapping
    public ResponseEntity<List<DeckDto>> getAllDecks() {
        List<DeckDto> decks = deckService.getAllDecks();
        return ResponseEntity.ok(decks);
    }

    @Operation(summary = "Récupère un deck par son ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Deck trouvé"),
            @ApiResponse(responseCode = "404", description = "Deck non trouvé",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<DeckDto> getDeckById(@PathVariable Long id) {
        DeckDto deck = deckService.getDeckById(id);
        return ResponseEntity.ok(deck);
    }

    @Operation(summary = "Crée un nouveau deck")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Deck créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<DeckDto> createDeck(@RequestBody DeckDto deckDto) {
        DeckDto createdDeck = deckService.createDeck(deckDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDeck);
    }

    @Operation(summary = "Met à jour un deck existant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Deck mis à jour"),
            @ApiResponse(responseCode = "404", description = "Deck non trouvé",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<DeckDto> updateDeck(@PathVariable Long id, @RequestBody DeckDto deckDto) {
        DeckDto updatedDeck = deckService.updateDeck(id, deckDto);
        return ResponseEntity.ok(updatedDeck);
    }

    @Operation(summary = "Supprime un deck")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Deck supprimé"),
            @ApiResponse(responseCode = "404", description = "Deck non trouvé",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeck(@PathVariable Long id) {
        deckService.deleteDeck(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Recherche des decks par nom")
    @GetMapping("/search")
    public ResponseEntity<List<DeckDto>> searchDecks(@RequestParam String name) {
        List<DeckDto> decks = deckService.searchDecksByName(name);
        return ResponseEntity.ok(decks);
    }
}