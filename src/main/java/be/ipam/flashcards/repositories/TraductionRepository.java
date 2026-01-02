package be.ipam.flashcards.repositories;

import be.ipam.flashcards.models.Traduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour les Traductions
 */
@Repository
public interface TraductionRepository extends JpaRepository<Traduction, Long> {

    List<Traduction> findByFlashcardId(Long flashcardId);
}