package be.ipam.flashcards.mappers;

import be.ipam.flashcards.dto.UtilisateurDto;
import be.ipam.flashcards.models.Utilisateur;
import org.springframework.stereotype.Component;

/**
 * MAPPER UTILISATEUR - Conversion manuelle
 */
@Component
public class UtilisateurMapper {

    // Convertit Utilisateur → UtilisateurDto (SANS le mot de passe)
    public UtilisateurDto toDto(Utilisateur utilisateur) {
        if (utilisateur == null) {
            return null;
        }

        UtilisateurDto dto = new UtilisateurDto();
        dto.setId(utilisateur.getId());
        dto.setEmail(utilisateur.getEmail());
        dto.setNom(utilisateur.getNom());
        dto.setPrenom(utilisateur.getPrenom());
        // Pas de mot de passe dans le DTO !

        return dto;
    }

    // Convertit UtilisateurDto → Utilisateur (pour création/mise à jour)
    public Utilisateur toEntity(UtilisateurDto dto) {
        if (dto == null) {
            return null;
        }

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail(dto.getEmail());
        utilisateur.setNom(dto.getNom());
        utilisateur.setPrenom(dto.getPrenom());
        // Le mot de passe sera géré séparément par le service

        return utilisateur;
    }
}