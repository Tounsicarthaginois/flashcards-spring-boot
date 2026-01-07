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

@RestController  // Controller REST qui renvoie du JSON
@RequestMapping("/api/decks")  // Base URL : /api/decks
@Tag(name = "Decks", description = "API de gestion des Decks")  // Groupe dans Swagger
public class DeckController {

    private final DeckService deckService;  // Service qui contient la logique métier

    public DeckController(DeckService deckService) {  // Injection de dépendance
        this.deckService = deckService;
    }

    @Operation(summary = "Liste tous les decks de l'utilisateur")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    })
    @GetMapping  // GET /api/decks - Liste MES decks (utilisateur connecté)
    public ResponseEntity<List<DeckDto>> getAllDecks() {
        List<DeckDto> decks = deckService.getAllDecks();  // Le service récupère l'user via JWT
        return ResponseEntity.ok(decks);  // 200 OK + liste
    }

    @Operation(summary = "Récupère un deck par son ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Deck trouvé"),
            @ApiResponse(responseCode = "404", description = "Deck non trouvé",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")  // GET /api/decks/5 - Récupère le deck avec ID=5
    public ResponseEntity<DeckDto> getDeckById(@PathVariable Long id) {  // @PathVariable récupère {id} de l'URL
        DeckDto deck = deckService.getDeckById(id);  // Cherche en DB
        return ResponseEntity.ok(deck);  // 200 + deck en JSON
    }

    @Operation(summary = "Crée un nouveau deck")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Deck créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping  // POST /api/decks - Crée un nouveau deck
    public ResponseEntity<DeckDto> createDeck(@RequestBody DeckDto deckDto) {  // Reçoit JSON du client
        DeckDto createdDeck = deckService.createDeck(deckDto);  // Service créé le deck + l'associe à l'user connecté
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDeck);  // 201 Created + deck créé
    }

    @Operation(summary = "Met à jour un deck existant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Deck mis à jour"),
            @ApiResponse(responseCode = "404", description = "Deck non trouvé",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")  // PUT /api/decks/5 - Modifie le deck 5
    public ResponseEntity<DeckDto> updateDeck(@PathVariable Long id, @RequestBody DeckDto deckDto) {  // ID dans URL + nouvelles données en JSON
        DeckDto updatedDeck = deckService.updateDeck(id, deckDto);  // Service vérifie que c'est bien MON deck
        return ResponseEntity.ok(updatedDeck);  // 200 + deck modifié
    }

    @Operation(summary = "Supprime un deck")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Deck supprimé"),
            @ApiResponse(responseCode = "404", description = "Deck non trouvé",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")  // DELETE /api/decks/5 - Supprime le deck 5
    public ResponseEntity<Void> deleteDeck(@PathVariable Long id) {  // Récupère l'ID depuis l'URL
        deckService.deleteDeck(id);  // Supprime le deck (+ cascade = supprime toutes ses flashcards)
        return ResponseEntity.noContent().build();  // 204 No Content (succès sans body)
    }

    @Operation(summary = "Recherche des decks par nom")
    @GetMapping("/search")  // GET /api/decks/search?name=toefl - Cherche les decks contenant "toefl"
    public ResponseEntity<List<DeckDto>> searchDecks(@RequestParam String name) {  // @RequestParam récupère ?name=xxx
        List<DeckDto> decks = deckService.searchDecksByName(name);  // SQL LIKE %name%
        return ResponseEntity.ok(decks);  // 200 + liste filtrée
    }
}

// Architecture MVC : Controller reçoit HTTP → appelle Service → Service utilise Repository → Repository accède DB