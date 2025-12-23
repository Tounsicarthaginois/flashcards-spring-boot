package be.ipam.flashcards.repositories;

import be.ipam.flashcards.models.Deck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeckRepository extends JpaRepository<Deck, Long> {
    List<Deck> findByUserId(Long userId);
    Optional<Deck> findByNameAndUserId(String name, Long userId);
    long countByUserId(Long userId);
    List<Deck> findByNameContainingIgnoreCase(String name);
    boolean existsByNameAndUserId(String name, Long userId);
}