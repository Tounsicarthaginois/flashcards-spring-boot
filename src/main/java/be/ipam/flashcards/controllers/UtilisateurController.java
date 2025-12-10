package be.ipam.flashcards.controllers;

import be.ipam.flashcards.dto.UtilisateurDto;
import be.ipam.flashcards.mappers.UtilisateurMapper;
import be.ipam.flashcards.models.Utilisateur;
import be.ipam.flashcards.services.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/utilisateurs")
@CrossOrigin(origins = "*")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    @Autowired
    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @GetMapping
    public ResponseEntity<List<UtilisateurDto>> getTousLesUtilisateurs() {
        List<Utilisateur> utilisateurs = utilisateurService.getTousLesUtilisateurs();

        List<UtilisateurDto> dtos = utilisateurs.stream()
                .map(UtilisateurMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UtilisateurDto> getUtilisateurParId(@PathVariable Long id) {
        Utilisateur utilisateur = utilisateurService.trouverParId(id);

        if (utilisateur == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(UtilisateurMapper.toDto(utilisateur));
    }

    @GetMapping("/par-email")
    public ResponseEntity<UtilisateurDto> getParEmail(@RequestParam String email) {
        Utilisateur utilisateur = utilisateurService.trouverParEmail(email);

        if (utilisateur == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(UtilisateurMapper.toDto(utilisateur));
    }

    @PostMapping
    public ResponseEntity<UtilisateurDto> creerUtilisateur(@RequestBody UtilisateurDto dto) {
        // Vérifier si l'email existe déjà
        if (utilisateurService.emailExiste(dto.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Utilisateur utilisateur = UtilisateurMapper.toEntity(dto);

        // CORRECTION : Définir un mot de passe par défaut temporaire
        utilisateur.setPasswordHash("temp123");

        Utilisateur sauvegarde = utilisateurService.ajouterUtilisateur(utilisateur);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(UtilisateurMapper.toDto(sauvegarde));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UtilisateurDto> mettreAJourUtilisateur(
            @PathVariable Long id,
            @RequestBody UtilisateurDto dto) {

        Utilisateur donnees = UtilisateurMapper.toEntity(dto);
        Utilisateur misAJour = utilisateurService.mettreAJourUtilisateur(id, donnees);

        if (misAJour == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(UtilisateurMapper.toDto(misAJour));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerUtilisateur(@PathVariable Long id) {
        Utilisateur utilisateur = utilisateurService.trouverParId(id);

        if (utilisateur == null) {
            return ResponseEntity.notFound().build();
        }

        utilisateurService.supprimerUtilisateur(id);
        return ResponseEntity.noContent().build();
    }
}