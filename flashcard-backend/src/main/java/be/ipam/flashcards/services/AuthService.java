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
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(
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
        if (utilisateurRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Un compte existe déjà avec cet email");
        }

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail(request.getEmail());
        utilisateur.setPassword(passwordEncoder.encode(request.getPassword()));
        utilisateur.setNom(request.getNom());
        utilisateur.setPrenom(request.getPrenom());
        utilisateur.setRole(Role.USER);

        utilisateurRepository.save(utilisateur);

        UserDetails userDetails = User.builder()
                .username(utilisateur.getEmail())
                .password(utilisateur.getPassword())
                .authorities("ROLE_" + utilisateur.getRole().name())
                .build();

        String token = jwtService.generateToken(userDetails);

        // userId inclus dans la réponse pour que le front puisse vérifier le créateur d'un deck
        return new AuthResponse(token, utilisateur.getEmail(), utilisateur.getRole().name(), utilisateur.getId());
    }

    // Connexion d'un utilisateur existant
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        Utilisateur utilisateur = utilisateurRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email ou mot de passe incorrect"));

        UserDetails userDetails = User.builder()
                .username(utilisateur.getEmail())
                .password(utilisateur.getPassword())
                .authorities("ROLE_" + utilisateur.getRole().name())
                .build();

        String token = jwtService.generateToken(userDetails);

        // userId inclus dans la réponse
        return new AuthResponse(token, utilisateur.getEmail(), utilisateur.getRole().name(), utilisateur.getId());
    }
}