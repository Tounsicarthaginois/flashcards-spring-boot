package be.ipam.flashcards.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "exemples")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Exemple {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")  // Type TEXT pour phrases longues
    private String phraseOriginal;  // "I eat an apple every day"

    @Column(nullable = false, columnDefinition = "TEXT")
    private String phraseTraduite;  // "Je mange une pomme chaque jour"

    @ManyToOne(fetch = FetchType.LAZY)  // Plusieurs exemples → Une traduction
    @JoinColumn(name = "traduction_id", nullable = false)  // FK vers table traductions
    private Traduction traduction;  // À quelle traduction appartient cet exemple
}

// Table la plus profonde dans la hiérarchie : Deck → Flashcard → Traduction → Exemple
// Permet de montrer comment utiliser le mot dans des phrases concrètes
// Hibernate génère : CREATE TABLE exemples (id, phrase_original, phrase_traduite, traduction_id)