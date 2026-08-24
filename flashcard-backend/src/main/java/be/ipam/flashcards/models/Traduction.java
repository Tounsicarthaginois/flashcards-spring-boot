package be.ipam.flashcards.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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

    @Column(nullable = false, columnDefinition = "TEXT")
    private String texte;  // "Pomme", "Bonjour", "Table"

    @ManyToOne(fetch = FetchType.LAZY)  // Plusieurs traductions → Une langue
    @JoinColumn(name = "langue_id", nullable = false)  // FK vers table langues
    private Langue langue;  // Dans quelle langue est cette traduction (ex: Français)

    @ManyToOne(fetch = FetchType.LAZY)  // Plusieurs traductions → Une flashcard
    @JoinColumn(name = "flashcard_id", nullable = false)  // FK vers table flashcards
    private Flashcard flashcard;  // Pour quelle flashcard est cette traduction

    @OneToMany(mappedBy = "traduction", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Exemple> exemples = new ArrayList<>();  // Une traduction peut avoir plusieurs exemples
    // cascade = CascadeType.ALL : Suppression traduction → suppression exemples
    // orphanRemoval = true : Retrait exemple de la liste → suppression DB
    // mappedBy = "traduction" : FK dans table exemples

}

// Niveau intermédiaire : Flashcard → Traduction → Exemple
// Permet d'avoir plusieurs traductions pour un même mot (ex: "Apple" → "Pomme" en FR + "Manzana" en ES)
// Chaque traduction peut avoir plusieurs exemples d'utilisation en phrases
// Hibernate génère : CREATE TABLE traductions (id, texte, langue_id, flashcard_id)