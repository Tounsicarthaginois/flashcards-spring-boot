package be.ipam.flashcards.controllers;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller pour les Progressions SRS
 */
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
    public ResponseEntity<ProgressionUtilisateurDto> enregistrerRevision(@RequestBody RevisionRequest request) {
        ProgressionUtilisateurDto progression = progressionService.enregistrerRevision(request);
        return ResponseEntity.ok(progression);
    }

    @Operation(summary = "Récupère les flashcards à réviser aujourd'hui")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    })
    @GetMapping("/a-reviser")
    public ResponseEntity<List<ProgressionUtilisateurDto>> getFlashcardsAReviser() {
        List<ProgressionUtilisateurDto> flashcards = progressionService.getFlashcardsAReviser();
        return ResponseEntity.ok(flashcards);
    }

    @Operation(summary = "Récupère toutes mes progressions")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    })
    @GetMapping("/mes-progressions")
    public ResponseEntity<List<ProgressionUtilisateurDto>> getMesProgressions() {
        List<ProgressionUtilisateurDto> progressions = progressionService.getMesProgressions();
        return ResponseEntity.ok(progressions);
    }
}