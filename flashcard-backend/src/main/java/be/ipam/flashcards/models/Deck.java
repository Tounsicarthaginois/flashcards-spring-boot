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
    private String name;  // "Vocabulaire TOEFL"

    @Column(columnDefinition = "TEXT")  // Type TEXT pour descriptions longues
    private String description;

    @Enumerated(EnumType.STRING)  // Stocke "OFFICIELLE", "PRIVEE", "PUBLIQUE" en texte
    @Column(nullable = false)
    private TypeListe type = TypeListe.PRIVEE;  // Par défaut PRIVEE

    @ManyToOne(fetch = FetchType.LAZY)  // Plusieurs decks → Une langue
    @JoinColumn(name = "langue_id", nullable = false)  // FK vers table langues
    private Langue langue;  // Quelle langue est étudiée dans ce deck

    @ManyToOne(fetch = FetchType.LAZY)  // Plusieurs decks → Un créateur
    @JoinColumn(name = "user_id", nullable = false)  // FK vers table utilisateurs
    private Utilisateur user;  // Qui a créé ce deck

    @ManyToOne(fetch = FetchType.LAZY)  // Plusieurs decks → Un validateur (nullable)
    @JoinColumn(name = "validateur_id")  // FK nullable vers utilisateurs
    private Utilisateur validateur;  // Qui a validé ce deck (si type = PUBLIQUE)

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "deck", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Flashcard> flashcards = new ArrayList<>();  // Un deck contient plusieurs flashcards
    // cascade = CascadeType.ALL : Si on supprime le deck, toutes ses flashcards sont supprimées
    // orphanRemoval = true : Si on retire une flashcard de la liste, elle est supprimée de la DB
    // mappedBy = "deck" : La FK est dans la table flashcards (colonne deck_id)

    @PrePersist  // Avant insertion en DB
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.type == null) {  // Sécurité
            this.type = TypeListe.PRIVEE;
        }
    }
}

// Table centrale : contient les listes de flashcards
// Relations : N-1 vers Langue, N-1 vers Utilisateur (créateur), N-1 vers Utilisateur (validateur), 1-N vers Flashcard