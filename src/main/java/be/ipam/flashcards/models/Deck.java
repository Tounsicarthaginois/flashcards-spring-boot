package be.ipam.flashcards.models;

import be.ipam.flashcards.enums.TypeListe;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ENTITÉ DECK - Représente une liste de flashcards
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

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Type de liste (OFFICIELLE, PRIVEE, PUBLIQUE)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeListe type = TypeListe.PRIVEE;

    // Langue étudiée (ex: Anglais, Français, etc.)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "langue_id", nullable = false)
    private Langue langue;

    // Créateur de la liste
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Utilisateur user;

    // Validateur (si liste publique)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validateur_id")
    private Utilisateur validateur;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Relation : Un deck contient plusieurs flashcards
    @OneToMany(mappedBy = "deck", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Flashcard> flashcards = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.type == null) {
            this.type = TypeListe.PRIVEE;
        }
    }
}