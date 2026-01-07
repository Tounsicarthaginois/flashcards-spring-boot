package be.ipam.flashcards.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "flashcards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Flashcard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;  // "Apple", "Hello", "Table" (le mot à apprendre)

    @ManyToOne(fetch = FetchType.LAZY)  // Plusieurs flashcards → Un deck
    @JoinColumn(name = "deck_id", nullable = false)  // FK vers table decks
    private Deck deck;  // Dans quel deck est cette flashcard

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "flashcard", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Traduction> traductions = new ArrayList<>();  // Une flashcard peut avoir plusieurs traductions
    // cascade = CascadeType.ALL : Suppression flashcard → suppression traductions
    // orphanRemoval = true : Retrait traduction de la liste → suppression DB
    // mappedBy = "flashcard" : FK dans table traductions

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

// Niveau intermédiaire : Deck → Flashcard → Traduction → Exemple
// Une flashcard peut avoir plusieurs traductions (ex: "Apple" → "Pomme" en FR + "Manzana" en ES)
// Hibernate génère : CREATE TABLE flashcards (id, question, deck_id, created_at)