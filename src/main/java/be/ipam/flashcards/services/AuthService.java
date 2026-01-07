package be.ipam.flashcards.services;

import be.ipam.flashcards.dto.AuthResponse;
import be.ipam.flashcards.dto.LoginRequest;
import be.ipam.flashcards.dto.RegisterRequest;
import be.ipam.flashcards.enums.Role;
import be.ipam.flashcards.models.Utilisateur;
import be.ipam.flashcards.repositories.UtilisateurRepository;
import be.ipam.flashcards.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;  // BCrypt
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(  // Injection par constructeur
                         UtilisateurRepository utilisateurRepository,
                         PasswordEncoder passwordEncoder,
                         JwtService jwtService,
                         AuthenticationManager authenticationManager
    ) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    // Inscription d'un nouvel utilisateur
    public AuthResponse register(RegisterRequest request) {
        // Vérifie que l'email n'est pas déjà utilisé
        if (utilisateurRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Un compte existe déjà avec cet email");
        }

        // Crée l'utilisateur
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail(request.getEmail());
        utilisateur.setPassword(passwordEncoder.encode(request.getPassword()));  // CRYPTE le password avec BCrypt
        utilisateur.setNom(request.getNom());
        utilisateur.setPrenom(request.getPrenom());
        utilisateur.setRole(Role.USER);  // Rôle par défaut

        // INSERT INTO utilisateurs
        utilisateurRepository.save(utilisateur);

        // Crée un objet UserDetails pour générer le token
        UserDetails userDetails = User.builder()
                .username(utilisateur.getEmail())  // Username = email
                .password(utilisateur.getPassword())  // Password crypté
                .authorities("ROLE_" + utilisateur.getRole().name())  // "ROLE_USER"
                .build();

        // Génère le token JWT (valable 24h)
        String token = jwtService.generateToken(userDetails);

        // Renvoie le token + infos utilisateur
        return new AuthResponse(token, utilisateur.getEmail(), utilisateur.getRole().name());
    }

    // Connexion d'un utilisateur existant
    public AuthResponse login(LoginRequest request) {
        // Authentifie l'utilisateur (vérifie email + password)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),  // Email
                        request.getPassword()  // Password en clair (sera vérifié avec BCrypt.matches())
                )
        );
        // Si authentification échoue → exception AuthenticationException (gérée par GlobalExceptionHandler)

        // Récupère l'utilisateur depuis la DB
        Utilisateur utilisateur = utilisateurRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email ou mot de passe incorrect"));

        // Crée UserDetails pour générer le token
        UserDetails userDetails = User.builder()
                .username(utilisateur.getEmail())
                .password(utilisateur.getPassword())
                .authorities("ROLE_" + utilisateur.getRole().name())  // "ROLE_USER" ou "ROLE_GESTIONNAIRE"
                .build();

        // Génère le token JWT
        String token = jwtService.generateToken(userDetails);

        // Renvoie le token + infos utilisateur
        return new AuthResponse(token, utilisateur.getEmail(), utilisateur.getRole().name());
    }
}

// Service central pour l'authentification
// register() : crée user + crypte password + génère token
// login() : vérifie credentials + génère token
// Le token JWT contient l'email et est signé (impossible à falsifier)
// Le client doit ensuite inclure ce token dans toutes ses requêtes : Authorization: Bearer <token>