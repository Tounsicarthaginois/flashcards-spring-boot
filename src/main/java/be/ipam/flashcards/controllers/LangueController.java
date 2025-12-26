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

/**
 * Controller pour les langues
 */
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
    @GetMapping
    public ResponseEntity<List<LangueDto>> getAllLangues() {
        List<LangueDto> langues = langueService.getAllLangues();
        return ResponseEntity.ok(langues);
    }

    @Operation(summary = "Récupère une langue par ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Langue trouvée"),
            @ApiResponse(responseCode = "404", description = "Langue non trouvée",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<LangueDto> getLangueById(@PathVariable Long id) {
        LangueDto langue = langueService.getLangueById(id);
        return ResponseEntity.ok(langue);
    }

    @Operation(summary = "Récupère une langue par code")
    @GetMapping("/code/{code}")
    public ResponseEntity<LangueDto> getLangueByCode(@PathVariable String code) {
        LangueDto langue = langueService.getLangueByCode(code);
        return ResponseEntity.ok(langue);
    }

    @Operation(summary = "Crée une nouvelle langue (Gestionnaire uniquement)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Langue créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Langue déjà existante",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @PreAuthorize("hasRole('GESTIONNAIRE')")
    @PostMapping
    public ResponseEntity<LangueDto> createLangue(@RequestBody LangueDto langueDto) {
        LangueDto createdLangue = langueService.createLangue(langueDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLangue);
    }

    @Operation(summary = "Met à jour une langue (Gestionnaire uniquement)")
    @PreAuthorize("hasRole('GESTIONNAIRE')")
    @PutMapping("/{id}")
    public ResponseEntity<LangueDto> updateLangue(@PathVariable Long id, @RequestBody LangueDto langueDto) {
        LangueDto updated = langueService.updateLangue(id, langueDto);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Supprime une langue (Gestionnaire uniquement)")
    @PreAuthorize("hasRole('GESTIONNAIRE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLangue(@PathVariable Long id) {
        langueService.deleteLangue(id);
        return ResponseEntity.noContent().build();
    }
}