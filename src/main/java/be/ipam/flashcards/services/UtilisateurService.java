package be.ipam.flashcards.services;

import be.ipam.flashcards.models.Utilisateur;
import be.ipam.flashcards.repositories.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service gérant la logique métier pour les utilisateurs
 */
@Service
@Transactional
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;

    @Autowired
    public UtilisateurService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    /**
     * Ajoute un nouvel utilisateur
     * @param utilisateur l'utilisateur à ajouter
     * @return l'utilisateur sauvegardé avec son ID
     */
    public Utilisateur ajouterUtilisateur(Utilisateur utilisateur) {
        return utilisateurRepository.save(utilisateur);
    }

    /**
     * Supprime un utilisateur par son ID
     * @param id l'ID de l'utilisateur à supprimer
     */
    public void supprimerUtilisateur(Long id) {
        utilisateurRepository.deleteById(id);
    }

    /**
     * Récupère tous les utilisateurs
     * @return la liste de tous les utilisateurs
     */
    @Transactional(readOnly = true)
    public List<Utilisateur> getTousLesUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    /**
     * Trouve un utilisateur par son email
     * @param email l'email à rechercher
     * @return l'utilisateur trouvé, ou null si non trouvé
     */
    @Transactional(readOnly = true)
    public Utilisateur trouverParEmail(String email) {
        Optional<Utilisateur> resultat = utilisateurRepository.findByEmail(email);
        return resultat.orElse(null);
    }

    /**
     * Trouve un utilisateur par son ID
     * @param id l'ID à rechercher
     * @return l'utilisateur trouvé, ou null si non trouvé
     */
    @Transactional(readOnly = true)
    public Utilisateur trouverParId(Long id) {
        Optional<Utilisateur> resultat = utilisateurRepository.findById(id);
        return resultat.orElse(null);
    }

    /**
     * Met à jour un utilisateur existant
     * @param id l'ID de l'utilisateur à mettre à jour
     * @param donnees les nouvelles données
     * @return l'utilisateur mis à jour, ou null si non trouvé
     */
    public Utilisateur mettreAJourUtilisateur(Long id, Utilisateur donnees) {
        Optional<Utilisateur> existantOpt = utilisateurRepository.findById(id);

        if (existantOpt.isEmpty()) {
            return null;
        }

        Utilisateur existant = existantOpt.get();

        // Mise à jour des champs (null-safe)
        if (donnees.getEmail() != null) {
            existant.setEmail(donnees.getEmail());
        }
        if (donnees.getDisplayName() != null) {
            existant.setDisplayName(donnees.getDisplayName());
        }
        if (donnees.getRole() != null) {
            existant.setRole(donnees.getRole());
        }
        // Le mot de passe doit être géré séparément avec une méthode dédiée
        if (donnees.getPasswordHash() != null) {
            existant.setPasswordHash(donnees.getPasswordHash());
        }

        return utilisateurRepository.save(existant);
    }

    /**
     * Vérifie si un email existe déjà
     * @param email l'email à vérifier
     * @return true si l'email existe, false sinon
     */
    @Transactional(readOnly = true)
    public boolean emailExiste(String email) {
        return utilisateurRepository.existsByEmail(email);
    }
}