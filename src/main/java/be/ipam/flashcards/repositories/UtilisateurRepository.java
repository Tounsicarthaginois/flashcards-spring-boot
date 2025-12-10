package be.ipam.flashcards.repositories;

import be.ipam.flashcards.models.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository pour l'entité Utilisateur
 * Fournit les méthodes CRUD de base via JpaRepository
 */
@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    /**
     * Trouve un utilisateur par son email
     * @param email l'email à rechercher
     * @return Optional contenant l'utilisateur s'il existe
     */
    Optional<Utilisateur> findByEmail(String email);

    /**
     * Vérifie si un email existe déjà dans la base
     * @param email l'email à vérifier
     * @return true si l'email existe, false sinon
     */
    boolean existsByEmail(String email);
}