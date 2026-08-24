package be.ipam.flashcards.enums;

/**
 * Rôles des utilisateurs dans l'application
 */
public enum Role {
    USER,           // Utilisateur normal (peut créer des listes privées)
    GESTIONNAIRE    // Gestionnaire (peut créer des listes officielles et valider les listes publiques)
}
