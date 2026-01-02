package be.ipam.flashcards.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ENTITÉ EXEMPLE - Représente une phrase exemple avec sa traduction
 */
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

    // Phrase exemple dans la langue étudiée
    @Column(nullable = false, columnDefinition = "TEXT")
    private String phraseOriginal;

    // Traduction de la phrase exemple
    @Column(nullable = false, columnDefinition = "TEXT")
    private String phraseTraduite;

    // Traduction associée
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "traduction_id", nullable = false)
    private Traduction traduction;
}
