package be.ipam.flashcards.services;

import be.ipam.flashcards.dto.ExempleDto;
import be.ipam.flashcards.dto.FlashcardDto;
import be.ipam.flashcards.dto.TraductionDto;
import be.ipam.flashcards.exception.ResourceNotFoundException;
import be.ipam.flashcards.mappers.FlashcardMapper;
import be.ipam.flashcards.models.*;
import be.ipam.flashcards.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Service pour gérer les flashcards
 */
@Service
public class FlashcardService {

    private final FlashcardRepository flashcardRepository;
    private final DeckRepository deckRepository;
    private final LangueRepository langueRepository;
    private final FlashcardMapper flashcardMapper;

    public FlashcardService(
            FlashcardRepository flashcardRepository,
            DeckRepository deckRepository,
            LangueRepository langueRepository,
            FlashcardMapper flashcardMapper
    ) {
        this.flashcardRepository = flashcardRepository;
        this.deckRepository = deckRepository;
        this.langueRepository = langueRepository;
        this.flashcardMapper = flashcardMapper;
    }

    // Récupère toutes les flashcards d'un deck
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

    // Crée une flashcard complète avec traductions et exemples
    @Transactional
    public FlashcardDto createFlashcard(FlashcardDto flashcardDto) {
        // Récupère le deck
        Deck deck = deckRepository.findById(flashcardDto.getDeckId())
                .orElseThrow(() -> new ResourceNotFoundException("Deck", "id", flashcardDto.getDeckId()));

        // Crée la flashcard
        Flashcard flashcard = flashcardMapper.toEntity(flashcardDto);
        flashcard.setDeck(deck);

        // Ajoute les traductions
        if (flashcardDto.getTraductions() != null) {
            List<Traduction> traductions = new ArrayList<>();

            for (TraductionDto tradDto : flashcardDto.getTraductions()) {
                Langue langue = langueRepository.findById(tradDto.getLangueId())
                        .orElseThrow(() -> new ResourceNotFoundException("Langue", "id", tradDto.getLangueId()));

                Traduction traduction = new Traduction();
                traduction.setTexte(tradDto.getTexte());
                traduction.setLangue(langue);
                traduction.setFlashcard(flashcard);

                // Ajoute les exemples
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

    // Supprime une flashcard
    @Transactional
    public void deleteFlashcard(Long id) {
        if (!flashcardRepository.existsById(id)) {
            throw new ResourceNotFoundException("Flashcard", "id", id);
        }
        flashcardRepository.deleteById(id);
    }
}
