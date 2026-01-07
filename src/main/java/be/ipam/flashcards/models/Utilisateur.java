package be.ipam.flashcards.models;

import be.ipam.flashcards.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "utilisateurs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)  // UNIQUE empêche deux users avec même email
    private String email;  // Login unique

    @Column(nullable = false)
    private String nom;  // Nom de famille

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false)
    private String password;  // TOUJOURS crypté avec BCrypt (jamais en clair en DB)

    @Enumerated(EnumType.STRING)  // Stocke "USER" ou "GESTIONNAIRE" en texte
    @Column(nullable = false)
    private Role role = Role.USER;  // Par défaut USER

    @Column(name = "created_at", updatable = false)  // Date fixe (pas modifiable)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Deck> decks = new ArrayList<>();  // Un utilisateur peut créer plusieurs decks
    // cascade = CascadeType.ALL : Suppression user → suppression decks (à vérifier si c'est voulu)
    // orphanRemoval = true : Retrait deck de la liste → suppression DB
    // mappedBy = "user" : FK dans table decks (colonne user_id)

    @PrePersist  // Exécuté AVANT l'insertion en DB
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.role == null) {  // Sécurité
            this.role = Role.USER;
        }
    }
}

// Table centrale : contient les utilisateurs de l'application
// password JAMAIS exposé dans les DTOs (sécurité)
// email UNIQUE garantit qu'on ne peut pas créer deux comptes avec le même email
// Hibernate génère : CREATE TABLE utilisateurs (id, email VARCHAR UNIQUE, nom, prenom, password, role, created_at)