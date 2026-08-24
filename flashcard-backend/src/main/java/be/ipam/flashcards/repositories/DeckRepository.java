package be.ipam.flashcards.repositories;

import be.ipam.flashcards.enums.TypeListe;
import be.ipam.flashcards.models.Deck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeckRepository extends JpaRepository<Deck, Long> {

    // SELECT * FROM decks WHERE user_id = ?
    List<Deck> findByUserId(Long userId);

    // SELECT * FROM decks WHERE user_id = ? AND name LIKE %?%
    List<Deck> findByUserIdAndNameContaining(Long userId, String name);

    // SELECT * FROM decks WHERE type = ? (ex: PUBLIQUE ou OFFICIELLE)
    List<Deck> findByType(TypeListe type);
}