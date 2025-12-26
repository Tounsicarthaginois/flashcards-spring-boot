package be.ipam.flashcards.repositories;

import be.ipam.flashcards.models.Deck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour les Decks
 */
@Repository
public interface DeckRepository extends JpaRepository<Deck, Long> {

    // Trouve tous les decks d'un utilisateur
    List<Deck> findByUserId(Long userId);

    // Recherche les decks d'un utilisateur par nom (contient)
    List<Deck> findByUserIdAndNameContaining(Long userId, String name);
}