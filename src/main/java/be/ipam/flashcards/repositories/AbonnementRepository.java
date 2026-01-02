package be.ipam.flashcards.repositories;

import be.ipam.flashcards.models.Abonnement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour les Abonnements
 */
@Repository
public interface AbonnementRepository extends JpaRepository<Abonnement, Long> {

    // Trouve les abonnements d'un utilisateur
    List<Abonnement> findByUtilisateurId(Long utilisateurId);

    // Vérifie si un utilisateur est abonné à un deck
    Optional<Abonnement> findByUtilisateurIdAndDeckId(Long utilisateurId, Long deckId);

    // Vérifie l'existence d'un abonnement
    boolean existsByUtilisateurIdAndDeckId(Long utilisateurId, Long deckId);
}
