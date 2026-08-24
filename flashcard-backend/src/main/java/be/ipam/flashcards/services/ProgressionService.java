package be.ipam.flashcards.services;

import be.ipam.flashcards.dto.FlashcardDto;
import be.ipam.flashcards.dto.ProgressionUtilisateurDto;
import be.ipam.flashcards.dto.RevisionRequest;
import be.ipam.flashcards.enums.EtatProgression;
import be.ipam.flashcards.exception.ResourceNotFoundException;
import be.ipam.flashcards.mappers.FlashcardMapper;
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
import java.util.Optional;

@Service
public class ProgressionService {

    private final ProgressionUtilisateurRepository progressionRepository;
    private final FlashcardRepository flashcardRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final FlashcardMapper flashcardMapper;

    public ProgressionService(
            ProgressionUtilisateurRepository progressionRepository,
            FlashcardRepository flashcardRepository,
            UtilisateurRepository utilisateurRepository,
            FlashcardMapper flashcardMapper
    ) {
        this.progressionRepository = progressionRepository;
        this.flashcardRepository = flashcardRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.flashcardMapper = flashcardMapper;
    }

    private Utilisateur getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "email", email));
    }

    // ============================================================
    // ALGORITHME SRS (Répétition Espacée)
    // ============================================================
    // ❌ Mauvaise réponse  → reset niveau 0, revoir demain (1 jour)
    // ✅ Niveau 1 (1ère bonne réponse) → revoir dans 1 jour
    // ✅ Niveau 2 (2ème bonne réponse) → revoir dans 7 jours
    // ✅ Niveau 3 (3ème bonne réponse) → revoir dans 30 jours (Maîtrisée)
    // ============================================================
    @Transactional
    public ProgressionUtilisateurDto enregistrerRevision(RevisionRequest request) {
        Utilisateur user = getCurrentUser();

        Flashcard flashcard = flashcardRepository.findById(request.getFlashcardId())
                .orElseThrow(() -> new ResourceNotFoundException("Flashcard", "id", request.getFlashcardId()));

        // Récupère la progression existante OU en crée une nouvelle (1ère révision)
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
            // ✅ BONNE RÉPONSE → monte d'un niveau
            int nouveauNiveau = progression.getNbRevisionsReussies() + 1;
            progression.setNbRevisionsReussies(nouveauNiveau);
            progression.setNiveauConnaissance(Math.min(5, progression.getNiveauConnaissance() + 1));

            if (nouveauNiveau == 1) {
                // Niveau 1 → revoir dans 1 jour
                progression.setProchaineRevision(LocalDateTime.now().plusDays(1));
                progression.setEtat(EtatProgression.EN_COURS);

            } else if (nouveauNiveau == 2) {
                // Niveau 2 → revoir dans 7 jours (1 semaine)
                progression.setProchaineRevision(LocalDateTime.now().plusDays(7));
                progression.setEtat(EtatProgression.EN_COURS);

            } else {
                // Niveau 3+ → revoir dans 30 jours (1 mois) — Maîtrisée
                progression.setProchaineRevision(LocalDateTime.now().plusDays(30));
                progression.setEtat(EtatProgression.CONNU);
            }

        } else {
            // ❌ MAUVAISE RÉPONSE → reset complet, revoir demain
            progression.setNbRevisionsReussies(0);
            progression.setNiveauConnaissance(Math.max(0, progression.getNiveauConnaissance() - 1));
            progression.setProchaineRevision(LocalDateTime.now().plusDays(1));
            progression.setEtat(EtatProgression.EN_COURS);
        }

        ProgressionUtilisateur saved = progressionRepository.save(progression);
        return toDto(saved);
    }

    // Cartes à réviser aujourd'hui pour un deck :
    // nouvelles (jamais révisées) + celles dont la date est passée
    public List<FlashcardDto> getCardsAReviserParDeck(Long deckId) {
        Utilisateur user = getCurrentUser();
        List<Flashcard> toutesLesCartes = flashcardRepository.findByDeckId(deckId);
        List<FlashcardDto> aReviser = new ArrayList<>();

        for (Flashcard card : toutesLesCartes) {
            Optional<ProgressionUtilisateur> prog = progressionRepository
                    .findByUtilisateurIdAndFlashcardId(user.getId(), card.getId());

            if (prog.isEmpty()) {
                // Jamais révisée → à inclure
                aReviser.add(flashcardMapper.toDto(card));
            } else if (prog.get().getProchaineRevision() == null ||
                    prog.get().getProchaineRevision().isBefore(LocalDateTime.now())) {
                // Date passée → due aujourd'hui
                aReviser.add(flashcardMapper.toDto(card));
            }
        }
        return aReviser;
    }

    // Progressions de l'utilisateur pour un deck (pour afficher les badges SRS)
    public List<ProgressionUtilisateurDto> getProgressionsByDeck(Long deckId) {
        Utilisateur user = getCurrentUser();
        List<Flashcard> cartesDuDeck = flashcardRepository.findByDeckId(deckId);
        List<ProgressionUtilisateurDto> result = new ArrayList<>();

        for (Flashcard card : cartesDuDeck) {
            progressionRepository
                    .findByUtilisateurIdAndFlashcardId(user.getId(), card.getId())
                    .ifPresent(prog -> result.add(toDto(prog)));
        }
        return result;
    }

    // Toutes les cartes dues aujourd'hui (tous decks confondus)
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

    // Toutes les progressions de l'utilisateur (stats globales)
    public List<ProgressionUtilisateurDto> getMesProgressions() {
        Utilisateur user = getCurrentUser();
        List<ProgressionUtilisateur> progressions = progressionRepository.findByUtilisateurId(user.getId());

        List<ProgressionUtilisateurDto> dtos = new ArrayList<>();
        for (ProgressionUtilisateur prog : progressions) {
            dtos.add(toDto(prog));
        }
        return dtos;
    }

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