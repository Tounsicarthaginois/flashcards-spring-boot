package be.ipam.flashcards.models;

import be.ipam.flashcards.enums.EtatProgression;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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

    @ManyToOne(fetch = FetchType.LAZY)  // Plusieurs progressions → Un utilisateur
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;  // Quel utilisateur

    @ManyToOne(fetch = FetchType.LAZY)  // Plusieurs progressions → Une flashcard
    @JoinColumn(name = "flashcard_id", nullable = false)
    private Flashcard flashcard;  // Quelle flashcard

    @Enumerated(EnumType.STRING)  // Stocke "NOUVEAU", "EN_COURS", "CONNU"
    @Column(nullable = false)
    private EtatProgression etat = EtatProgression.NOUVEAU;  // État par défaut

    @Column(nullable = false)
    private Integer niveauConnaissance = 0;  // 0 = nouveau, 5 = maîtrisé

    @Column(name = "prochaine_revision")
    private LocalDateTime prochaineRevision;  // Calculé par l'algorithme SRS (1j, 7j, 30j)

    @Column(name = "nb_revisions_reussies")
    private Integer nbRevisionsReussies = 0;  // Compteur de succès consécutifs

    @Column(name = "derniere_revision")
    private LocalDateTime derniereRevision;  // Dernière fois que l'user a révisé

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist  // Initialise les valeurs par défaut avant insertion
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

// Table centrale du système SRS (répétition espacée)
// Table intermédiaire N-N : Un utilisateur révise plusieurs flashcards, une flashcard est révisée par plusieurs utilisateurs
// Contient toutes les stats nécessaires pour l'algorithme de révision espacée
// Hibernate génère : CREATE TABLE progressions (id, utilisateur_id, flashcard_id, etat, niveau_connaissance, prochaine_revision, nb_revisions_reussies, derniere_revision, created_at)