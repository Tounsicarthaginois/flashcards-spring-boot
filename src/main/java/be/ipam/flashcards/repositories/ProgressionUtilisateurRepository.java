package be.ipam.flashcards.repositories;

import be.ipam.flashcards.enums.EtatProgression;
import be.ipam.flashcards.models.ProgressionUtilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour les Progressions
 */
@Repository
public interface ProgressionUtilisateurRepository extends JpaRepository<ProgressionUtilisateur, Long> {

    // Trouve la progression d'un utilisateur pour une flashcard
    Optional<ProgressionUtilisateur> findByUtilisateurIdAndFlashcardId(Long utilisateurId, Long flashcardId);

    // Trouve toutes les progressions d'un utilisateur
    List<ProgressionUtilisateur> findByUtilisateurId(Long utilisateurId);

    // Trouve les flashcards à réviser aujourd'hui
    List<ProgressionUtilisateur> findByUtilisateurIdAndProchaineRevisionBefore(Long utilisateurId, LocalDateTime date);

    // Trouve les flashcards par état
    List<ProgressionUtilisateur> findByUtilisateurIdAndEtat(Long utilisateurId, EtatProgression etat);
}
