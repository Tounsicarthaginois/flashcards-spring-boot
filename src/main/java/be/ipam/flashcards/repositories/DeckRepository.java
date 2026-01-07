package be.ipam.flashcards.repositories;

import be.ipam.flashcards.models.Deck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeckRepository extends JpaRepository<Deck, Long> {

    // Spring génère : SELECT * FROM decks WHERE user_id = ?
    List<Deck> findByUserId(Long userId);

    // Spring génère : SELECT * FROM decks WHERE user_id = ? AND name LIKE %?%
    List<Deck> findByUserIdAndNameContaining(Long userId, String name);
    // "Containing" = LIKE %valeur% (recherche partielle)
}

// Convention Spring Data JPA :
// - "findBy" = SELECT
// - "UserId" = colonne user_id (camelCase → snake_case automatique)
// - "And" = AND en SQL
// - "Containing" = LIKE %?% (recherche dans la chaîne)
// Autres mots-clés possibles : StartingWith (LIKE ?%), EndingWith (LIKE %?), IgnoreCase, OrderBy...