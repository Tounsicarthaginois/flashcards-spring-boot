package be.ipam.flashcards.exception;

/**
 * Exception personnalisée pour les ressources non trouvées (erreur 404)
 *
 * Utilisée quand on cherche un élément par ID et qu'il n'existe pas
 * Exemple : Chercher le deck avec id=999 qui n'existe pas
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructeur avec un message formaté
     *
     * @param resourceName Le nom de la ressource (ex: "Deck", "User")
     * @param fieldName Le nom du champ (ex: "id", "email")
     * @param fieldValue La valeur recherchée (ex: 999, "test@test.com")
     *
     * Exemple d'utilisation :
     * throw new ResourceNotFoundException("Deck", "id", 5);
     * Message généré : "Deck not found with id : '5'"
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
    }
}
