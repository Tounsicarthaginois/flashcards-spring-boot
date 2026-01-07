package be.ipam.flashcards.repositories;

import be.ipam.flashcards.models.Flashcard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlashcardRepository extends JpaRepository<Flashcard, Long> {

    // Spring génère : SELECT * FROM flashcards WHERE deck_id = ?
    List<Flashcard> findByDeckId(Long deckId);
}

// Repository simple - récupère toutes les flashcards d'un deck
// Utilisé par FlashcardService pour afficher toutes les cartes d'une liste