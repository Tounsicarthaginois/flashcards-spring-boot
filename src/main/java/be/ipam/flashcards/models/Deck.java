package be.ipam.flashcards.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ENTITÉ DECK - Représente un paquet de flashcards
 * Un Deck = comme un "dossier" qui contient plusieurs flashcards sur un thème
 */
@Entity
@Table(name = "decks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Deck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_public")
    private boolean isPublic = false;

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    // RELATION : Un deck appartient à UN utilisateur
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Utilisateur user;

    // RELATION : Un deck contient PLUSIEURS flashcards
    @OneToMany(mappedBy = "deck", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Flashcard> flashcards = new ArrayList<>();

    // Date de création automatique
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Méthode pour ajouter une flashcard
    public void addFlashcard(Flashcard flashcard) {
        this.flashcards.add(flashcard);
        flashcard.setDeck(this);
    }

    // Méthode pour retirer une flashcard
    public void removeFlashcard(Flashcard flashcard) {
        this.flashcards.remove(flashcard);
        flashcard.setDeck(null);
    }

    // Nombre de flashcards dans ce deck
    public int getFlashcardCount() {
        return this.flashcards != null ? this.flashcards.size() : 0;
    }
}
