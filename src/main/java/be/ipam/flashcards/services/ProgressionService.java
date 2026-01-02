package be.ipam.flashcards.services;

import be.ipam.flashcards.dto.ProgressionUtilisateurDto;
import be.ipam.flashcards.dto.RevisionRequest;
import be.ipam.flashcards.enums.EtatProgression;
import be.ipam.flashcards.exception.ResourceNotFoundException;
import be.ipam.flashcards.models.Flashcard;
import be.ipam.flashcards.models.ProgressionUtilisateur;
import be.ipam.flashcards.models.Utilisateur;
import be.ipam.flashcards.repositories.FlashcardRepository;
import be.ipam.flashcards.repositories.ProgressionUtilisateurRepository;
import be.ipam.flashcards.repositories.UtilisateurRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service pour gérer les progressions SRS
 */
@Service
public class ProgressionService {

    private final ProgressionUtilisateurRepository progressionRepository;
    private final FlashcardRepository flashcardRepository;
    private final UtilisateurRepository utilisateurRepository;

    public ProgressionService(
            ProgressionUtilisateurRepository progressionRepository,
            FlashcardRepository flashcardRepository,
            UtilisateurRepository utilisateurRepository
    ) {
        this.progressionRepository = progressionRepository;
        this.flashcardRepository = flashcardRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    // Récupère l'utilisateur connecté
    private Utilisateur getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "email", email));
    }

    // Enregistre une révision (succès ou échec)
    @Transactional
    public ProgressionUtilisateurDto enregistrerRevision(RevisionRequest request) {
        Utilisateur user = getCurrentUser();

        Flashcard flashcard = flashcardRepository.findById(request.getFlashcardId())
                .orElseThrow(() -> new ResourceNotFoundException("Flashcard", "id", request.getFlashcardId()));

        // Récupère ou crée la progression
        ProgressionUtilisateur progression = progressionRepository
                .findByUtilisateurIdAndFlashcardId(user.getId(), flashcard.getId())
                .orElseGet(() -> {
                    ProgressionUtilisateur newProg = new ProgressionUtilisateur();
                    newProg.setUtilisateur(user);
                    newProg.setFlashcard(flashcard);
                    newProg.setEtat(EtatProgression.NOUVEAU);
                    newProg.setNiveauConnaissance(0);
                    newProg.setNbRevisionsReussies(0);
                    return newProg;
                });

        progression.setDerniereRevision(LocalDateTime.now());

        if (request.getReussi()) {
            // Révision réussie
            progression.setNbRevisionsReussies(progression.getNbRevisionsReussies() + 1);
            progression.setNiveauConnaissance(Math.min(5, progression.getNiveauConnaissance() + 1));

            // Calcul de la prochaine révision selon l'algorithme SRS
            if (progression.getNbRevisionsReussies() == 1) {
                // 1ère révision réussie → 30 jours
                progression.setProchaineRevision(LocalDateTime.now().plusDays(30));
                progression.setEtat(EtatProgression.EN_COURS);
            } else if (progression.getNbRevisionsReussies() >= 2) {
                // 2ème révision réussie → 60 jours
                progression.setProchaineRevision(LocalDateTime.now().plusDays(60));
                progression.setEtat(EtatProgression.CONNU);
            } else {
                // 1er apprentissage → 1 jour
                progression.setProchaineRevision(LocalDateTime.now().plusDays(1));
            }
        } else {
            // Révision échouée → retour à 1 jour
            progression.setNbRevisionsReussies(0);
            progression.setNiveauConnaissance(Math.max(0, progression.getNiveauConnaissance() - 1));
            progression.setProchaineRevision(LocalDateTime.now().plusDays(1));
            progression.setEtat(EtatProgression.EN_COURS);
        }

        ProgressionUtilisateur saved = progressionRepository.save(progression);
        return toDto(saved);
    }

    // Récupère les flashcards à réviser aujourd'hui
    public List<ProgressionUtilisateurDto> getFlashcardsAReviser() {
        Utilisateur user = getCurrentUser();
        List<ProgressionUtilisateur> progressions = progressionRepository
                .findByUtilisateurIdAndProchaineRevisionBefore(user.getId(), LocalDateTime.now());

        List<ProgressionUtilisateurDto> dtos = new ArrayList<>();
        for (ProgressionUtilisateur prog : progressions) {
            dtos.add(toDto(prog));
        }
        return dtos;
    }

    // Récupère toutes les progressions de l'utilisateur
    public List<ProgressionUtilisateurDto> getMesProgressions() {
        Utilisateur user = getCurrentUser();
        List<ProgressionUtilisateur> progressions = progressionRepository.findByUtilisateurId(user.getId());

        List<ProgressionUtilisateurDto> dtos = new ArrayList<>();
        for (ProgressionUtilisateur prog : progressions) {
            dtos.add(toDto(prog));
        }
        return dtos;
    }

    // Convertit une entité en DTO
    private ProgressionUtilisateurDto toDto(ProgressionUtilisateur progression) {
        ProgressionUtilisateurDto dto = new ProgressionUtilisateurDto();
        dto.setId(progression.getId());
        dto.setUtilisateurId(progression.getUtilisateur().getId());
        dto.setFlashcardId(progression.getFlashcard().getId());
        dto.setQuestion(progression.getFlashcard().getQuestion());
        dto.setEtat(progression.getEtat());
        dto.setNiveauConnaissance(progression.getNiveauConnaissance());
        dto.setProchaineRevision(progression.getProchaineRevision());
        dto.setNbRevisionsReussies(progression.getNbRevisionsReussies());
        dto.setDerniereRevision(progression.getDerniereRevision());
        dto.setCreatedAt(progression.getCreatedAt());
        return dto;
    }
}
