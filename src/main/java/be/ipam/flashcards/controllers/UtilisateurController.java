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

    @Operation(summary = "Liste tous les utilisateurs")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    })
    @GetMapping  // GET /api/utilisateurs - Liste tous les utilisateurs (SANS password)
    public ResponseEntity<List<UtilisateurDto>> getAllUtilisateurs() {
        List<UtilisateurDto> utilisateurs = utilisateurService.getAllUtilisateurs();  // Renvoie UtilisateurDto (pas d'entité directe)
        return ResponseEntity.ok(utilisateurs);  // DTO garantit que password n'est jamais exposé
    }

    @Operation(summary = "Récupère un utilisateur par ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateur trouvé"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")  // GET /api/utilisateurs/5 - Cherche par ID
    public ResponseEntity<UtilisateurDto> getUtilisateurById(@PathVariable Long id) {
        UtilisateurDto utilisateur = utilisateurService.getUtilisateurById(id);
        return ResponseEntity.ok(utilisateur);  // 200 + utilisateur (email, nom, prenom, role...)
    }

    @Operation(summary = "Récupère un utilisateur par email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateur trouvé"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/par-email")  // GET /api/utilisateurs/par-email?email=test@test.com
    public ResponseEntity<UtilisateurDto> getUtilisateurByEmail(@RequestParam String email) {  // @RequestParam récupère ?email=xxx
        UtilisateurDto utilisateur = utilisateurService.getUtilisateurByEmail(email);  // Cherche par email unique
        return ResponseEntity.ok(utilisateur);
    }

    @Operation(summary = "Met à jour un utilisateur")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateur mis à jour"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")  // PUT /api/utilisateurs/5 - Modifie l'utilisateur 5
    public ResponseEntity<UtilisateurDto> updateUtilisateur(
            @PathVariable Long id,  // ID dans l'URL
            @RequestBody UtilisateurDto utilisateurDto) {  // Nouvelles données en JSON
        UtilisateurDto updated = utilisateurService.updateUtilisateur(id, utilisateurDto);  // Met à jour nom, prenom, etc.
        return ResponseEntity.ok(updated);  // 200 + utilisateur modifié
    }

    @Operation(summary = "Supprime un utilisateur")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Utilisateur supprimé"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")  // DELETE /api/utilisateurs/5 - Supprime l'utilisateur 5
    public ResponseEntity<Void> deleteUtilisateur(@PathVariable Long id) {
        utilisateurService.deleteUtilisateur(id);  // Supprime l'utilisateur (attention : ses decks, progressions aussi ?)
        return ResponseEntity.noContent().build();  // 204 No Content
    }
}

// Important : UtilisateurDto ne contient JAMAIS le password (sécurité)
// Le mapper Entity → DTO exclut automatiquement le password