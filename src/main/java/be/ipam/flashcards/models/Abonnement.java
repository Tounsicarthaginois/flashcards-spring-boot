package be.ipam.flashcards.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity  // Table JPA
@Table(name = "abonnements")  // Nom de la table en DB
@Getter  // Lombok génère les getters
@Setter  // Lombok génère les setters
@NoArgsConstructor  // Constructeur vide (obligatoire pour JPA)
@AllArgsConstructor  // Constructeur avec tous les champs
public class Abonnement {

    @Id  // Clé primaire
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-incrémentation
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)  // Un utilisateur peut s'abonner à plusieurs decks
    @JoinColumn(name = "utilisateur_id", nullable = false)  // Colonne FK en DB
    private Utilisateur utilisateur;

    @ManyToOne(fetch = FetchType.LAZY)  // Un deck peut avoir plusieurs abonnés
    @JoinColumn(name = "deck_id", nullable = false)  // Colonne FK en DB
    private Deck deck;

    @Column(name = "nouveaux_mots_par_jour", nullable = false)  // Paramètre d'apprentissage
    private Integer nouveauxMotsParJour = 10;  // Valeur par défaut

    @Column(name = "date_abonnement", nullable = false, updatable = false)  // Date fixe
    private LocalDateTime dateAbonnement;

    @PrePersist  // Méthode exécutée AVANT l'insertion en DB
    protected void onCreate() {
        this.dateAbonnement = LocalDateTime.now();  // Date du jour
        if (this.nouveauxMotsParJour == null) {  // Sécurité
            this.nouveauxMotsParJour = 10;
        }
    }
}

// Table intermédiaire N-N : Utilisateur × Deck
// Permet de paramétrer l'apprentissage (combien de nouveaux mots par jour)
// Hibernate génère : CREATE TABLE abonnements (id, utilisateur_id, deck_id, nouveaux_mots_par_jour, date_abonnement)