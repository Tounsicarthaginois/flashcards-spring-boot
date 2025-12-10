package be.ipam.flashcards.mappers;

import be.ipam.flashcards.dto.UtilisateurDto;
import be.ipam.flashcards.models.Utilisateur;

/**
 * Classe utilitaire pour convertir entre Entité Utilisateur et DTO UtilisateurDto
 */
public class UtilisateurMapper {

    // Empêcher l'instanciation
    private UtilisateurMapper() {
    }

    /**
     * Convertit une entité Utilisateur en DTO
     * @param utilisateur l'entité à convertir
     * @return le DTO correspondant, ou null si l'entité est null
     */
    public static UtilisateurDto toDto(Utilisateur utilisateur) {
        if (utilisateur == null) {
            return null;
        }

        return new UtilisateurDto(
                utilisateur.getId(),
                utilisateur.getEmail(),
                utilisateur.getDisplayName(),
                utilisateur.getRole()
        );
    }

    /**
     * Convertit un DTO en entité Utilisateur
     * Note : le passwordHash n'est pas géré ici pour des raisons de sécurité
     * @param dto le DTO à convertir
     * @return l'entité correspondante, ou null si le DTO est null
     */
    public static Utilisateur toEntity(UtilisateurDto dto) {
        if (dto == null) {
            return null;
        }

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(dto.getId());
        utilisateur.setEmail(dto.getEmail());
        utilisateur.setDisplayName(dto.getDisplayName());
        utilisateur.setRole(dto.getRole());
        // passwordHash doit être géré séparément lors de la création/modification

        return utilisateur;
    }
}