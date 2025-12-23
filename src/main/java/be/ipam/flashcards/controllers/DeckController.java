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
 * CONTROLLER DECK - API REST pour les Decks
 */
@RestController
@RequestMapping("/api/decks")
@Tag(name = "Decks", description = "API de gestion des Decks")
public class DeckController {

    private final DeckService deckService;

    public DeckController(DeckService deckService) {
        this.deckService = deckService;
    }

    // GET /api/decks - Liste tous les decks
    @Operation(summary = "Liste tous les decks de l'utilisateur")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès",
                    content = @Content(schema = @Schema(implementation = DeckDto.class))),
            @ApiResponse(responseCode = "500", description = "Erreur serveur",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<DeckDto>> getAllDecks() {
        Long userId = 1L;  // TODO: Récupérer depuis l'authentification
        List<DeckDto> decks = deckService.getAllDecksByUser(userId);
        return ResponseEntity.ok(decks);
    }

    // GET /api/decks/{id} - Détails d'un deck
    @Operation(summary = "Récupère un deck par son ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Deck trouvé",
                    content = @Content(schema = @Schema(implementation = DeckDto.class))),
            @ApiResponse(responseCode = "404", description = "Deck non trouvé",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<DeckDto> getDeckById(@PathVariable("id") Long id) {
        Long userId = 1L;  // TODO: Récupérer depuis l'authentification
        DeckDto deck = deckService.getDeckById(id, userId);
        return ResponseEntity.ok(deck);
    }

    // POST /api/decks - Crée un deck
    @Operation(summary = "Crée un nouveau deck")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Deck créé avec succès",
                    content = @Content(schema = @Schema(implementation = DeckDto.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<DeckDto> createDeck(@RequestBody DeckDto deckDto) {
        Long userId = 1L;  // TODO: Récupérer depuis l'authentification
        DeckDto createdDeck = deckService.createDeck(deckDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDeck);
    }

    // PUT /api/decks/{id} - Modifie un deck
    @Operation(summary = "Met à jour un deck existant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Deck modifié avec succès",
                    content = @Content(schema = @Schema(implementation = DeckDto.class))),
            @ApiResponse(responseCode = "404", description = "Deck non trouvé",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<DeckDto> updateDeck(@PathVariable("id") Long id,
                                              @RequestBody DeckDto deckDto) {
        Long userId = 1L;  // TODO: Récupérer depuis l'authentification
        DeckDto updatedDeck = deckService.updateDeck(id, deckDto, userId);
        return ResponseEntity.ok(updatedDeck);
    }

    // DELETE /api/decks/{id} - Supprime un deck
    @Operation(summary = "Supprime un deck")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Deck supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Deck non trouvé",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeck(@PathVariable("id") Long id) {
        Long userId = 1L;  // TODO: Récupérer depuis l'authentification
        deckService.deleteDeck(id, userId);
        return ResponseEntity.noContent().build();
    }

    // GET /api/decks/search?name=XXX - Recherche par nom
    @Operation(summary = "Recherche des decks par nom")
    @GetMapping("/search")
    public ResponseEntity<List<DeckDto>> searchDecks(@RequestParam("name") String name) {
        Long userId = 1L;  // TODO: Récupérer depuis l'authentification
        List<DeckDto> decks = deckService.searchDecksByName(name, userId);
        return ResponseEntity.ok(decks);
    }
}