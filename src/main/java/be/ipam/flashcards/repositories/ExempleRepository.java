package be.ipam.flashcards.repositories;

import be.ipam.flashcards.models.Exemple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour les Exemples
 */
@Repository
public interface ExempleRepository extends JpaRepository<Exemple, Long> {

    List<Exemple> findByTraductionId(Long traductionId);
}