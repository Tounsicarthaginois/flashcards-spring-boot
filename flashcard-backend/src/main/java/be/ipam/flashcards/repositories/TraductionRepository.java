package be.ipam.flashcards.repositories;

import be.ipam.flashcards.models.Traduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TraductionRepository extends JpaRepository<Traduction, Long> {

    // Spring génère : SELECT * FROM traductions WHERE flashcard_id = ?
    List<Traduction> findByFlashcardId(Long flashcardId);
}

// Repository simple - récupère toutes les traductions d'une flashcard
// Mais généralement, on charge les traductions via Flashcard.getTraductions() (@OneToMany)
// Ce repository est surtout utilisé pour sauvegarder les traductions individuellement