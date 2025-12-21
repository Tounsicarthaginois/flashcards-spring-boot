package be.ipam.flashcards.repositories;

import be.ipam.flashcards.models.Deck;
import be.ipam.flashcards.models.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * REPOSITORY DECK - Interface pour accéder aux données des Decks
 */
@Repository
public interface DeckRepository extends JpaRepository<Deck, Long> {

    // Trouve tous les decks d'un utilisateur
    List<Deck> findByUser(Utilisateur user);

    // Trouve tous les decks d'un utilisateur par son ID
    List<Deck> findByUserId(Long userId);

    // Trouve tous les decks publics
    List<Deck> findByIsPublicTrue();

    // Trouve un deck par nom et utilisateur (pour éviter les doublons)
    Optional<Deck> findByNameAndUserId(String name, Long userId);

    // Compte le nombre de decks d'un utilisateur
    long countByUserId(Long userId);

    // Recherche par nom (contient le texte)
    List<Deck> findByNameContainingIgnoreCase(String name);

    // Vérifie si un deck existe avec ce nom pour cet utilisateur
    boolean existsByNameAndUserId(String name, Long userId);
}