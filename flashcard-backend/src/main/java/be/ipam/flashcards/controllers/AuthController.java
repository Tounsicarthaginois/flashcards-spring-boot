package be.ipam.flashcards.controllers;

// Imports des DTOs (objets qu'on envoie/reçoit)
import be.ipam.flashcards.dto.AuthResponse;
import be.ipam.flashcards.dto.LoginRequest;
import be.ipam.flashcards.dto.RegisterRequest;
import be.ipam.flashcards.exception.ErrorResponse;
import be.ipam.flashcards.services.AuthService;

// Imports Swagger pour documenter l'API
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

// Imports Spring MVC
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController  // Dit à Spring que c'est un contrôleur REST (renvoie du JSON)
@RequestMapping("/api/auth")  // Toutes les routes commencent par /api/auth
@Tag(name = "Authentification", description = "Endpoints pour l'inscription et la connexion")  // Groupe dans Swagger
public class AuthController {

    private final AuthService authService;  // Service qui fait le vrai travail (logique métier)

    public AuthController(AuthService authService) {  // Injection de dépendance par constructeur
        this.authService = authService;
    }

    @Operation(summary = "Inscription d'un nouvel utilisateur")  // Description dans Swagger
    @ApiResponses({  // Définit les réponses possibles pour Swagger
            @ApiResponse(responseCode = "201", description = "Utilisateur créé avec succès",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Email déjà utilisé",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/register")  // Route POST /api/auth/register
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {  // @RequestBody = reçoit du JSON
        AuthResponse response = authService.register(request);  // Appelle le service pour traiter
        return ResponseEntity.status(HttpStatus.CREATED).body(response);  // Renvoie 201 + token JWT
    }

    @Operation(summary = "Connexion d'un utilisateur")  // Description Swagger
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Connexion réussie",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Email ou mot de passe incorrect",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/login")  // Route POST /api/auth/login
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {  // Reçoit email + password en JSON
        AuthResponse response = authService.login(request);  // Vérifie les credentials + génère token
        return ResponseEntity.ok(response);  // Renvoie 200 + token JWT
    }
}

// Ces routes sont PUBLIQUES (pas de JWT nécessaire) - configuré dans SecurityConfig