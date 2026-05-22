package be.ipam.flashcards.controllers;

import be.ipam.flashcards.dto.UtilisateurDto;
import be.ipam.flashcards.exception.ErrorResponse;
import be.ipam.flashcards.services.UtilisateurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
@Tag(name = "Utilisateurs", description = "API de gestion des utilisateurs")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @Operation(summary = "Liste tous les utilisateurs (GESTIONNAIRE uniquement)")
    @PreAuthorize("hasRole('GESTIONNAIRE')")  // Seul l'admin peut voir tous les comptes
    @GetMapping
    public ResponseEntity<List<UtilisateurDto>> getAllUtilisateurs() {
        return ResponseEntity.ok(utilisateurService.getAllUtilisateurs());
    }

    @Operation(summary = "Récupère un utilisateur par ID (GESTIONNAIRE uniquement)")
    @PreAuthorize("hasRole('GESTIONNAIRE')")  // Seul l'admin peut chercher un compte par ID
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateur trouvé"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<UtilisateurDto> getUtilisateurById(@PathVariable Long id) {
        return ResponseEntity.ok(utilisateurService.getUtilisateurById(id));
    }

    @Operation(summary = "Récupère un utilisateur par email (GESTIONNAIRE uniquement)")
    @PreAuthorize("hasRole('GESTIONNAIRE')")  // Seul l'admin peut chercher par email
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateur trouvé"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/par-email")
    public ResponseEntity<UtilisateurDto> getUtilisateurByEmail(@RequestParam String email) {
        return ResponseEntity.ok(utilisateurService.getUtilisateurByEmail(email));
    }

    @Operation(summary = "Met à jour un utilisateur (GESTIONNAIRE uniquement)")
    @PreAuthorize("hasRole('GESTIONNAIRE')")  // Seul l'admin peut modifier un compte
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateur mis à jour"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<UtilisateurDto> updateUtilisateur(
            @PathVariable Long id,
            @RequestBody UtilisateurDto utilisateurDto) {
        return ResponseEntity.ok(utilisateurService.updateUtilisateur(id, utilisateurDto));
    }

    @Operation(summary = "Supprime un utilisateur (GESTIONNAIRE uniquement)")
    @PreAuthorize("hasRole('GESTIONNAIRE')")  // Seul l'admin peut supprimer un compte
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Utilisateur supprimé"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUtilisateur(@PathVariable Long id) {
        utilisateurService.deleteUtilisateur(id);
        return ResponseEntity.noContent().build();
    }
}