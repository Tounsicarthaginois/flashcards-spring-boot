package be.ipam.flashcards.repositories;

import be.ipam.flashcards.models.Langue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository pour les langues
 */
@Repository
public interface LangueRepository extends JpaRepository<Langue, Long> {

    Optional<Langue> findByCode(String code);

    Optional<Langue> findByNom(String nom);

    boolean existsByCode(String code);

    boolean existsByNom(String nom);
}