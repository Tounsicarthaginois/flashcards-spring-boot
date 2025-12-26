package be.ipam.flashcards.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ENTITÉ LANGUE - Représente une langue (Français, Anglais, etc.)
 */
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

    @Column(nullable = false, unique = true, length = 100)
    private String nom;  // Français, Anglais, Espagnol...

    @Column(nullable = false, unique = true, length = 5)
    private String code;  // fr, en, es...
}
