package be.ipam.flashcards.services;

import be.ipam.flashcards.dto.UtilisateurDto;
import be.ipam.flashcards.exception.ResourceNotFoundException;
import be.ipam.flashcards.mappers.UtilisateurMapper;
import be.ipam.flashcards.models.Utilisateur;
import be.ipam.flashcards.repositories.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final UtilisateurMapper utilisateurMapper;

    public UtilisateurService(UtilisateurRepository utilisateurRepository, UtilisateurMapper utilisateurMapper) {
        this.utilisateurRepository = utilisateurRepository;
        this.utilisateurMapper = utilisateurMapper;
    }

    // Liste tous les utilisateurs (SANS passwords)
    public List<UtilisateurDto> getAllUtilisateurs() {
        List<Utilisateur> utilisateurs = utilisateurRepository.findAll();  // SELECT * FROM utilisateurs
        return utilisateurs.stream()  // Stream Java 8
                .map(utilisateurMapper::toDto)  // Convertit chaque Utilisateur en UtilisateurDto (sans password)
                .collect(Collectors.toList());  // Collecte dans une liste
    }

    // Récupère UN utilisateur par ID
    public UtilisateurDto getUtilisateurById(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "id", id));
        return utilisateurMapper.toDto(utilisateur);  // Convertit en DTO (sans password)
    }

    // Récupère UN utilisateur par email
    public UtilisateurDto getUtilisateurByEmail(String email) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "email", email));
        return utilisateurMapper.toDto(utilisateur);  // Sans password
    }

    @Transactional
    public UtilisateurDto updateUtilisateur(Long id, UtilisateurDto utilisateurDto) {
        // Récupère l'utilisateur existant
        Utilisateur existingUtilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "id", id));

        // Met à jour les champs modifiables (PAS le password ici)
        existingUtilisateur.setNom(utilisateurDto.getNom());
        existingUtilisateur.setPrenom(utilisateurDto.getPrenom());
        existingUtilisateur.setEmail(utilisateurDto.getEmail());
        // Note : Le password n'est pas modifiable via ce service (sécurité)
        // Pour changer le password, il faudrait un endpoint dédié avec l'ancien password

        Utilisateur updatedUtilisateur = utilisateurRepository.save(existingUtilisateur);  // UPDATE utilisateurs
        return utilisateurMapper.toDto(updatedUtilisateur);  // Renvoie sans password
    }

    @Transactional
    public void deleteUtilisateur(Long id) {
        if (!utilisateurRepository.existsById(id)) {  // Vérifie existence
            throw new ResourceNotFoundException("Utilisateur", "id", id);
        }
        utilisateurRepository.deleteById(id);  // DELETE FROM utilisateurs
        // ATTENTION : Si cascade sur decks/progressions, ça les supprime aussi
    }
}

// Service simple pour gérer les utilisateurs
// IMPORTANT : Le mapper exclut toujours le password des DTOs (sécurité)
// updateUtilisateur() ne modifie PAS le password (faudrait un endpoint dédié avec ancien password)
// Utilisé principalement pour lister/consulter les users (pas pour l'authentification, c'est AuthService)