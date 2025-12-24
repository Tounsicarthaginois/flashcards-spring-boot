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

/**
 * SERVICE UTILISATEUR - Logique métier pour les utilisateurs
 */
@Service
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final UtilisateurMapper utilisateurMapper;

    public UtilisateurService(UtilisateurRepository utilisateurRepository, UtilisateurMapper utilisateurMapper) {
        this.utilisateurRepository = utilisateurRepository;
        this.utilisateurMapper = utilisateurMapper;
    }

    // Récupère tous les utilisateurs
    public List<UtilisateurDto> getAllUtilisateurs() {
        List<Utilisateur> utilisateurs = utilisateurRepository.findAll();
        return utilisateurs.stream()
                .map(utilisateurMapper::toDto)
                .collect(Collectors.toList());
    }

    // Récupère un utilisateur par ID
    public UtilisateurDto getUtilisateurById(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "id", id));
        return utilisateurMapper.toDto(utilisateur);
    }

    // Récupère un utilisateur par email
    public UtilisateurDto getUtilisateurByEmail(String email) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "email", email));
        return utilisateurMapper.toDto(utilisateur);
    }

    // Met à jour un utilisateur (sans le mot de passe)
    @Transactional
    public UtilisateurDto updateUtilisateur(Long id, UtilisateurDto utilisateurDto) {
        Utilisateur existingUtilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "id", id));

        // Mise à jour des champs (pas le mot de passe)
        existingUtilisateur.setNom(utilisateurDto.getNom());
        existingUtilisateur.setPrenom(utilisateurDto.getPrenom());
        existingUtilisateur.setEmail(utilisateurDto.getEmail());

        Utilisateur updatedUtilisateur = utilisateurRepository.save(existingUtilisateur);
        return utilisateurMapper.toDto(updatedUtilisateur);
    }

    // Supprime un utilisateur
    @Transactional
    public void deleteUtilisateur(Long id) {
        if (!utilisateurRepository.existsById(id)) {
            throw new ResourceNotFoundException("Utilisateur", "id", id);
        }
        utilisateurRepository.deleteById(id);
    }
}