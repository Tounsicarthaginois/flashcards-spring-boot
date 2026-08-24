package be.ipam.flashcards.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "langues")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Langue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)  // UNIQUE empêche les doublons
    private String nom;  // "Français", "Anglais", "Espagnol"

    @Column(nullable = false, unique = true, length = 5)  // Code court (2-3 caractères)
    private String code;  // "fr", "en", "es" (code ISO 639-1)
}

// Table de référence simple
// UNIQUE garantit qu'on ne peut pas créer deux fois "Français" ou deux fois "fr"
// Pas de @PrePersist (pas de valeurs par défaut à initialiser)
// Seuls les GESTIONNAIRE peuvent créer/modifier/supprimer des langues
// Hibernate génère : CREATE TABLE langues (id, nom VARCHAR(100) UNIQUE, code VARCHAR(5) UNIQUE)