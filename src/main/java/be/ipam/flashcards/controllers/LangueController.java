package be.ipam.flashcards.controllers;

import be.ipam.flashcards.dto.LangueDto;
import be.ipam.flashcards.exception.ErrorResponse;
import be.ipam.flashcards.services.LangueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/langues")
@Tag(name = "Langues", description = "API de gestion des langues")
public class LangueController {

    private final LangueService langueService;

    public LangueController(LangueService langueService) {
        this.langueService = langueService;
    }

    @Operation(summary = "Liste toutes les langues")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    })
    @GetMapping  // GET /api/langues - Accessible à tous (USER et GESTIONNAIRE)
    public ResponseEntity<List<LangueDto>> getAllLangues() {
        List<LangueDto> langues = langueService.getAllLangues();  // Français, Anglais, Espagnol...
        return ResponseEntity.ok(langues);
    }

    @Operation(summary = "Récupère une langue par ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Langue trouvée"),
            @ApiResponse(responseCode = "404", description = "Langue non trouvée",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")  // GET /api/langues/1 - Cherche par ID
    public ResponseEntity<LangueDto> getLangueById(@PathVariable Long id) {
        LangueDto langue = langueService.getLangueById(id);
        return ResponseEntity.ok(langue);
    }

    @Operation(summary = "Récupère une langue par code")
    @GetMapping("/code/{code}")  // GET /api/langues/code/fr - Cherche par code ("fr", "en", "es"...)
    public ResponseEntity<LangueDto> getLangueByCode(@PathVariable String code) {
        LangueDto langue = langueService.getLangueByCode(code);  // Utile pour chercher rapidement
        return ResponseEntity.ok(langue);
    }

    @Operation(summary = "Crée une nouvelle langue (Gestionnaire uniquement)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Langue créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Langue déjà existante",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @PreAuthorize("hasRole('GESTIONNAIRE')")  // PROTÉGÉ : Seuls les GESTIONNAIRE peuvent créer des langues
    @PostMapping  // POST /api/langues - Si USER essaie → 403 Forbidden
    public ResponseEntity<LangueDto> createLangue(@RequestBody LangueDto langueDto) {
        LangueDto createdLangue = langueService.createLangue(langueDto);  // Ajoute nouvelle langue
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLangue);  // 201 Created
    }

    @Operation(summary = "Met à jour une langue (Gestionnaire uniquement)")
    @PreAuthorize("hasRole('GESTIONNAIRE')")  // PROTÉGÉ : Seuls GESTIONNAIRE
    @PutMapping("/{id}")  // PUT /api/langues/1
    public ResponseEntity<LangueDto> updateLangue(@PathVariable Long id, @RequestBody LangueDto langueDto) {
        LangueDto updated = langueService.updateLangue(id, langueDto);  // Modifie nom ou code
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Supprime une langue (Gestionnaire uniquement)")
    @PreAuthorize("hasRole('GESTIONNAIRE')")  // PROTÉGÉ : Seuls GESTIONNAIRE
    @DeleteMapping("/{id}")  // DELETE /api/langues/1
    public ResponseEntity<Void> deleteLangue(@PathVariable Long id) {
        langueService.deleteLangue(id);  // Supprime la langue
        return ResponseEntity.noContent().build();  // 204 No Content
    }
}

// @PreAuthorize vérifié par Spring Security AVANT d'exécuter la méthode
// Si rôle incorrect → exception AccessDeniedException → 403 Forbidden