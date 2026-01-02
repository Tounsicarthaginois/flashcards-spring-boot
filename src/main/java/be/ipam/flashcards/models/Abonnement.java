package be.ipam.flashcards.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * ENTITÉ ABONNEMENT - Représente l'abonnement d'un utilisateur à un deck
 */
@Entity
@Table(name = "abonnements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Abonnement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Utilisateur abonné
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    // Deck auquel l'utilisateur est abonné
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deck_id", nullable = false)
    private Deck deck;

    // Nombre de nouveaux mots par jour (paramètre ajustable)
    @Column(name = "nouveaux_mots_par_jour", nullable = false)
    private Integer nouveauxMotsParJour = 10;

    @Column(name = "date_abonnement", nullable = false, updatable = false)
    private LocalDateTime dateAbonnement;

    @PrePersist
    protected void onCreate() {
        this.dateAbonnement = LocalDateTime.now();
        if (this.nouveauxMotsParJour == null) {
            this.nouveauxMotsParJour = 10;
        }
    }
}
