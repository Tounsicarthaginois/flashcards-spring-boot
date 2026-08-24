package be.ipam.flashcards.repositories;

import be.ipam.flashcards.models.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    // Spring génère : SELECT * FROM utilisateurs WHERE email = ?
    Optional<Utilisateur> findByEmail(String email);
    // Utilisé pour la connexion (récupère user par email pour vérifier password)
    // Optional évite NullPointerException si email n'existe pas

    // Spring génère : SELECT EXISTS(SELECT 1 FROM utilisateurs WHERE email = ?)
    boolean existsByEmail(String email);
    // Vérifie si email existe avant inscription (pour rejeter doublons)
}

// Repository pour authentification et gestion utilisateurs
// findByEmail est crucial pour AuthService (login + loadUserByUsername pour Spring Security)
// existsByEmail évite d'essayer d'insérer un email déjà existant (même si UNIQUE en DB protège)