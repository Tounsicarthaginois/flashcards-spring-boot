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

    // Récupère l'utilisateur connecté via JWT
    private Utilisateur getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "email", email));
    }

    // CŒUR DU SYSTÈME SRS : Enregistre une révision et calcule la prochaine date
    @Transactional
    public ProgressionUtilisateurDto enregistrerRevision(RevisionRequest request) {
        Utilisateur user = getCurrentUser();

        // Vérifie que la flashcard existe
        Flashcard flashcard = flashcardRepository.findById(request.getFlashcardId())
                .orElseThrow(() -> new ResourceNotFoundException("Flashcard", "id", request.getFlashcardId()));

        // Récupère la progression existante OU crée une nouvelle si 1ère révision
        ProgressionUtilisateur progression = progressionRepository
                .findByUtilisateurIdAndFlashcardId(user.getId(), flashcard.getId())
                .orElseGet(() -> {  // Si pas trouvé, crée une nouvelle progression
                    ProgressionUtilisateur newProg = new ProgressionUtilisateur();
                    newProg.setUtilisateur(user);
                    newProg.setFlashcard(flashcard);
                    newProg.setEtat(EtatProgression.NOUVEAU);
                    newProg.setNiveauConnaissance(0);
                    newProg.setNbRevisionsReussies(0);
                    return newProg;
                });

        // Marque la date de dernière révision
        progression.setDerniereRevision(LocalDateTime.now());

        if (request.getReussi()) {  // ✅ RÉVISION RÉUSSIE
            // Incrémente le compteur de succès
            progression.setNbRevisionsReussies(progression.getNbRevisionsReussies() + 1);
            // Augmente le niveau (max 5)
            progression.setNiveauConnaissance(Math.min(5, progression.getNiveauConnaissance() + 1));

            // ALGORITHME SRS : Calcul de la prochaine révision
            if (progression.getNbRevisionsReussies() == 1) {
                // 1ère révision réussie → 30 jours
                progression.setProchaineRevision(LocalDateTime.now().plusDays(30));
                progression.setEtat(EtatProgression.EN_COURS);
            } else if (progression.getNbRevisionsReussies() >= 2) {
                // 2ème révision réussie (ou +) → 60 jours (CONNU)
                progression.setProchaineRevision(LocalDateTime.now().plusDays(60));
                progression.setEtat(EtatProgression.CONNU);
            } else {
                // Première fois (nbRevisionsReussies == 0 avant incrémentation) → 1 jour
                progression.setProchaineRevision(LocalDateTime.now().plusDays(1));
            }
        } else {  // ❌ RÉVISION ÉCHOUÉE
            // Reset le compteur à 0
            progression.setNbRevisionsReussies(0);
            // Baisse le niveau (min 0)
            progression.setNiveauConnaissance(Math.max(0, progression.getNiveauConnaissance() - 1));
            // Retour à 1 jour
            progression.setProchaineRevision(LocalDateTime.now().plusDays(1));
            progression.setEtat(EtatProgression.EN_COURS);
        }

        // Sauvegarde (INSERT ou UPDATE)
        ProgressionUtilisateur saved = progressionRepository.save(progression);
        return toDto(saved);
    }

    // Récupère les flashcards à réviser AUJOURD'HUI (prochaine_revision <= maintenant)
    public List<ProgressionUtilisateurDto> getFlashcardsAReviser() {
        Utilisateur user = getCurrentUser();
        List<ProgressionUtilisateur> progressions = progressionRepository
                .findByUtilisateurIdAndProchaineRevisionBefore(user.getId(), LocalDateTime.now());  // WHERE prochaine_revision < NOW()

        List<ProgressionUtilisateurDto> dtos = new ArrayList<>();
        for (ProgressionUtilisateur prog : progressions) {
            dtos.add(toDto(prog));
        }
        return dtos;
    }

    // Récupère TOUTES les progressions de l'utilisateur (stats globales)
    public List<ProgressionUtilisateurDto> getMesProgressions() {
        Utilisateur user = getCurrentUser();
        List<ProgressionUtilisateur> progressions = progressionRepository.findByUtilisateurId(user.getId());

        List<ProgressionUtilisateurDto> dtos = new ArrayList<>();
        for (ProgressionUtilisateur prog : progressions) {
            dtos.add(toDto(prog));
        }
        return dtos;
    }

    // Convertit ProgressionUtilisateur → ProgressionUtilisateurDto
    private ProgressionUtilisateurDto toDto(ProgressionUtilisateur progression) {
        ProgressionUtilisateurDto dto = new ProgressionUtilisateurDto();
        dto.setId(progression.getId());
        dto.setUtilisateurId(progression.getUtilisateur().getId());
        dto.setFlashcardId(progression.getFlashcard().getId());
        dto.setQuestion(progression.getFlashcard().getQuestion());  // "Apple" (pour affichage)
        dto.setEtat(progression.getEtat());  // NOUVEAU / EN_COURS / CONNU
        dto.setNiveauConnaissance(progression.getNiveauConnaissance());  // 0-5
        dto.setProchaineRevision(progression.getProchaineRevision());  // Date calculée
        dto.setNbRevisionsReussies(progression.getNbRevisionsReussies());  // Compteur
        dto.setDerniereRevision(progression.getDerniereRevision());
        dto.setCreatedAt(progression.getCreatedAt());
        return dto;
    }
}

// SERVICE LE PLUS IMPORTANT : Implémente l'algorithme SRS (répétition espacée)
// Algorithme : 1ère réussite → 30j, 2ème réussite → 60j (CONNU), échec → 1j
// Basé sur la courbe d'oubli d'Ebbinghaus : réviser juste avant d'oublier
// enregistrerRevision() est appelé après chaque révision pour calculer la prochaine date