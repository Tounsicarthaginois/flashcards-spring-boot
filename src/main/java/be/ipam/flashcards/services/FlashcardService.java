package be.ipam.flashcards.services;

import be.ipam.flashcards.dto.ExempleDto;
import be.ipam.flashcards.dto.FlashcardDto;
import be.ipam.flashcards.dto.TraductionDto;
import be.ipam.flashcards.enums.Role;
import be.ipam.flashcards.exception.ResourceNotFoundException;
import be.ipam.flashcards.mappers.FlashcardMapper;
import be.ipam.flashcards.models.*;
import be.ipam.flashcards.repositories.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class FlashcardService {

    private final FlashcardRepository flashcardRepository;
    private final DeckRepository deckRepository;
    private final LangueRepository langueRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final FlashcardMapper flashcardMapper;

    public FlashcardService(
            FlashcardRepository flashcardRepository,
            DeckRepository deckRepository,
            LangueRepository langueRepository,
            UtilisateurRepository utilisateurRepository,
            FlashcardMapper flashcardMapper
    ) {
        this.flashcardRepository = flashcardRepository;
        this.deckRepository = deckRepository;
        this.langueRepository = langueRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.flashcardMapper = flashcardMapper;
    }

    // Récupère l'utilisateur connecté via le token JWT
    private Utilisateur getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "email", email));
    }

    // Récupère toutes les flashcards d'un deck
    // Accessible à tous les utilisateurs connectés (pour voir les decks officiels)
    public List<FlashcardDto> getFlashcardsByDeckId(Long deckId) {
        List<Flashcard> flashcards = flashcardRepository.findByDeckId(deckId);
        return flashcardMapper.toDtoList(flashcards);
    }

    // Récupère une flashcard par ID
    public FlashcardDto getFlashcardById(Long id) {
        Flashcard flashcard = flashcardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flashcard", "id", id));
        return flashcardMapper.toDto(flashcard);
    }

    @Transactional
    public FlashcardDto createFlashcard(FlashcardDto flashcardDto) {
        Utilisateur currentUser = getCurrentUser();

        // Vérifie que le deck existe
        Deck deck = deckRepository.findById(flashcardDto.getDeckId())
                .orElseThrow(() -> new ResourceNotFoundException("Deck", "id", flashcardDto.getDeckId()));

        // SÉCURITÉ : vérifie que l'utilisateur est le propriétaire du deck
        // Exception : les GESTIONNAIRE peuvent ajouter des cartes partout
        if (!deck.getUser().getId().equals(currentUser.getId())
                && !currentUser.getRole().equals(Role.GESTIONNAIRE)) {
            throw new IllegalArgumentException(
                    "Vous n'êtes pas autorisé à ajouter une flashcard dans ce deck"
            );
        }

        // Crée la flashcard
        Flashcard flashcard = flashcardMapper.toEntity(flashcardDto);
        flashcard.setDeck(deck);

        // Crée les traductions avec leurs exemples
        if (flashcardDto.getTraductions() != null) {
            List<Traduction> traductions = new ArrayList<>();

            for (TraductionDto tradDto : flashcardDto.getTraductions()) {
                Langue langue = langueRepository.findById(tradDto.getLangueId())
                        .orElseThrow(() -> new ResourceNotFoundException("Langue", "id", tradDto.getLangueId()));

                Traduction traduction = new Traduction();
                traduction.setTexte(tradDto.getTexte());
                traduction.setLangue(langue);
                traduction.setFlashcard(flashcard);

                if (tradDto.getExemples() != null) {
                    List<Exemple> exemples = new ArrayList<>();
                    for (ExempleDto exDto : tradDto.getExemples()) {
                        Exemple exemple = new Exemple();
                        exemple.setPhraseOriginal(exDto.getPhraseOriginal());
                        exemple.setPhraseTraduite(exDto.getPhraseTraduite());
                        exemple.setTraduction(traduction);
                        exemples.add(exemple);
                    }
                    traduction.setExemples(exemples);
                }

                traductions.add(traduction);
            }
            flashcard.setTraductions(traductions);
        }

        Flashcard savedFlashcard = flashcardRepository.save(flashcard);
        return flashcardMapper.toDto(savedFlashcard);
    }

    @Transactional
    public void deleteFlashcard(Long id) {
        Utilisateur currentUser = getCurrentUser();

        // Vérifie que la flashcard existe
        Flashcard flashcard = flashcardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flashcard", "id", id));

        // SÉCURITÉ : vérifie que l'utilisateur est le propriétaire du deck parent
        // Exception : les GESTIONNAIRE peuvent tout supprimer
        if (!flashcard.getDeck().getUser().getId().equals(currentUser.getId())
                && !currentUser.getRole().equals(Role.GESTIONNAIRE)) {
            throw new IllegalArgumentException(
                    "Vous n'êtes pas autorisé à supprimer cette flashcard"
            );
        }

        flashcardRepository.deleteById(id);
    }
}