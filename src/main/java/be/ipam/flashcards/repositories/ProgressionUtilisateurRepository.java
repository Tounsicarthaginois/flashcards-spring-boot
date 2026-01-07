package be.ipam.flashcards.repositories;

import be.ipam.flashcards.enums.EtatProgression;
import be.ipam.flashcards.models.ProgressionUtilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProgressionUtilisateurRepository extends JpaRepository<ProgressionUtilisateur, Long> {

    // Spring génère : SELECT * FROM progressions WHERE utilisateur_id = ? AND flashcard_id = ?
    Optional<ProgressionUtilisateur> findByUtilisateurIdAndFlashcardId(Long utilisateurId, Long flashcardId);
    // Récupère LA progression d'un user pour UNE flashcard spécifique

    // Spring génère : SELECT * FROM progressions WHERE utilisateur_id = ?
    List<ProgressionUtilisateur> findByUtilisateurId(Long utilisateurId);
    // Toutes les progressions d'un utilisateur

    // Spring génère : SELECT * FROM progressions WHERE utilisateur_id = ? AND prochaine_revision < ?
    List<ProgressionUtilisateur> findByUtilisateurIdAndProchaineRevisionBefore(Long utilisateurId, LocalDateTime date);
    // IMPORTANT : Flashcards à réviser AUJOURD'HUI (date < maintenant)
    // "Before" = < en SQL (pas <=)

    // Spring génère : SELECT * FROM progressions WHERE utilisateur_id = ? AND etat = ?
    List<ProgressionUtilisateur> findByUtilisateurIdAndEtat(Long utilisateurId, EtatProgression etat);
    // Filtre par état (NOUVEAU, EN_COURS, CONNU)
}

// Repository central du système SRS
// findByUtilisateurIdAndProchaineRevisionBefore est la méthode clé pour GET /api/progressions/a-reviser
// Utilisée avec LocalDateTime.now() pour récupérer les cartes dont la date est passée