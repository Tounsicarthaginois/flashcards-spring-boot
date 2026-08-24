package be.ipam.flashcards.repositories;

import be.ipam.flashcards.models.Abonnement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository  // Indique à Spring que c'est un repository
public interface AbonnementRepository extends JpaRepository<Abonnement, Long> {  // Hérite méthodes CRUD gratuites
    // JpaRepository<Abonnement, Long> = Type d'entité + Type de la clé primaire

    // Spring génère automatiquement : SELECT * FROM abonnements WHERE utilisateur_id = ?
    List<Abonnement> findByUtilisateurId(Long utilisateurId);

    // Spring génère : SELECT * FROM abonnements WHERE utilisateur_id = ? AND deck_id = ?
    Optional<Abonnement> findByUtilisateurIdAndDeckId(Long utilisateurId, Long deckId);

    // Spring génère : SELECT EXISTS(SELECT 1 FROM abonnements WHERE utilisateur_id = ? AND deck_id = ?)
    boolean existsByUtilisateurIdAndDeckId(Long utilisateurId, Long deckId);
}

// Interface (pas de @Override nécessaire) - Spring Data JPA génère l'implémentation automatiquement
// Méthodes gratuites héritées : save(), findById(), findAll(), deleteById(), count()...
// Méthodes custom : Spring comprend le nom et génère le SQL automatiquement
// "findBy" = SELECT, "existsBy" = SELECT EXISTS, "And" = AND en SQL