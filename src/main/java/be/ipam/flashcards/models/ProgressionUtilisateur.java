package be.ipam.flashcards.models;

import be.ipam.flashcards.enums.EtatProgression;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * ENTITÉ PROGRESSION UTILISATEUR - Suit la progression d'un utilisateur pour une flashcard
 */
@Entity
@Table(name = "progressions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProgressionUtilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Utilisateur
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    // Flashcard
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flashcard_id", nullable = false)
    private Flashcard flashcard;

    // État actuel
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EtatProgression etat = EtatProgression.NOUVEAU;

    // Niveau de connaissance (0-5)
    @Column(nullable = false)
    private Integer niveauConnaissance = 0;

    // Date de prochaine révision
    @Column(name = "prochaine_revision")
    private LocalDateTime prochaineRevision;

    // Nombre de révisions réussies consécutives
    @Column(name = "nb_revisions_reussies")
    private Integer nbRevisionsReussies = 0;

    // Dernière révision
    @Column(name = "derniere_revision")
    private LocalDateTime derniereRevision;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.etat == null) {
            this.etat = EtatProgression.NOUVEAU;
        }
        if (this.niveauConnaissance == null) {
            this.niveauConnaissance = 0;
        }
        if (this.nbRevisionsReussies == null) {
            this.nbRevisionsReussies = 0;
        }
    }
}
