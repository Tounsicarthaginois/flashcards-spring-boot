package be.ipam.flashcards.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * ENTITÉ TRADUCTION - Représente une traduction d'une flashcard
 */
@Entity
@Table(name = "traductions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Traduction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Texte traduit (dans la langue de l'apprenant)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String texte;

    // Langue de cette traduction
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "langue_id", nullable = false)
    private Langue langue;

    // Flashcard associée
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flashcard_id", nullable = false)
    private Flashcard flashcard;

    // Exemples pour cette traduction
    @OneToMany(mappedBy = "traduction", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Exemple> exemples = new ArrayList<>();
}
