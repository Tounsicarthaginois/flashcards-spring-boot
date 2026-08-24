package be.ipam.flashcards.repositories;

import be.ipam.flashcards.models.Exemple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExempleRepository extends JpaRepository<Exemple, Long> {

    // Spring génère : SELECT * FROM exemples WHERE traduction_id = ?
    List<Exemple> findByTraductionId(Long traductionId);
}

// Repository simple - une seule méthode custom
// Utilisé pour récupérer tous les exemples d'une traduction spécifique
// Mais généralement, on charge les exemples directement via Traduction.getExemples() (relation @OneToMany)