package be.ipam.flashcards.repositories;

import be.ipam.flashcards.models.Langue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LangueRepository extends JpaRepository<Langue, Long> {

    // Spring génère : SELECT * FROM langues WHERE code = ?
    Optional<Langue> findByCode(String code);  // Recherche par code ("fr", "en", "es")

    // Spring génère : SELECT * FROM langues WHERE nom = ?
    Optional<Langue> findByNom(String nom);  // Recherche par nom ("Français", "Anglais")

    // Spring génère : SELECT EXISTS(SELECT 1 FROM langues WHERE code = ?)
    boolean existsByCode(String code);  // Vérifie si le code existe déjà (pour éviter doublons)

    // Spring génère : SELECT EXISTS(SELECT 1 FROM langues WHERE nom = ?)
    boolean existsByNom(String nom);  // Vérifie si le nom existe déjà
}

// Repository pour gérer les langues disponibles
// existsByCode/Nom utilisés avant création pour empêcher doublons (même si UNIQUE en DB fait la vérif)
// Optional = peut retourner null si pas trouvé (évite NullPointerException)