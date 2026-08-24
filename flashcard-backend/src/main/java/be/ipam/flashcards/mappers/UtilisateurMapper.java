package be.ipam.flashcards.mappers;

import be.ipam.flashcards.dto.UtilisateurDto;
import be.ipam.flashcards.models.Utilisateur;
import org.springframework.stereotype.Component;

@Component
public class UtilisateurMapper {

    // Convertit Utilisateur (DB) → UtilisateurDto (API) SANS password
    public UtilisateurDto toDto(Utilisateur utilisateur) {
        if (utilisateur == null) {
            return null;
        }

        UtilisateurDto dto = new UtilisateurDto();
        dto.setId(utilisateur.getId());
        dto.setEmail(utilisateur.getEmail());
        dto.setNom(utilisateur.getNom());
        dto.setPrenom(utilisateur.getPrenom());
        // IMPORTANT : Pas de password dans le DTO ! (sécurité)
        // Même crypté, on ne l'expose jamais dans l'API

        return dto;
    }

    // Convertit UtilisateurDto (API) → Utilisateur (DB)
    public Utilisateur toEntity(UtilisateurDto dto) {
        if (dto == null) {
            return null;
        }

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail(dto.getEmail());
        utilisateur.setNom(dto.getNom());
        utilisateur.setPrenom(dto.getPrenom());
        // Note : password et role sont gérés dans AuthService (cryptage BCrypt, rôle par défaut)

        return utilisateur;
    }
}

// Mapper critique pour la sécurité : exclut le password du DTO
// Utilisé par AuthService et UtilisateurService
// Différence avec les autres mappers : pas de méthode toDtoList() (pas souvent besoin de lister tous les users)