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
        List<Flashcard> flashcards = flashcardRepository.findByDeckId(deckId);  // SELECT WHERE deck_id = ?
        return flashcardMapper.toDtoList(flashcards);  // Convertit avec toute la structure imbriquée
    }

    // Récupère UNE flashcard par ID
    public FlashcardDto getFlashcardById(Long id) {
        Flashcard flashcard = flashcardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flashcard", "id", id));
        return flashcardMapper.toDto(flashcard);  // Avec traductions et exemples
    }

    @Transactional  // Important : toute la création doit réussir ou échouer ensemble
    public FlashcardDto createFlashcard(FlashcardDto flashcardDto) {
        // 1. Vérifie que le deck existe
        Deck deck = deckRepository.findById(flashcardDto.getDeckId())
                .orElseThrow(() -> new ResourceNotFoundException("Deck", "id", flashcardDto.getDeckId()));

        // 2. Crée la flashcard (juste la question)
        Flashcard flashcard = flashcardMapper.toEntity(flashcardDto);
        flashcard.setDeck(deck);  // Définit la relation @ManyToOne

        // 3. Crée les traductions (si présentes)
        if (flashcardDto.getTraductions() != null) {
            List<Traduction> traductions = new ArrayList<>();

            for (TraductionDto tradDto : flashcardDto.getTraductions()) {  // Boucle sur chaque traduction
                // Vérifie que la langue existe
                Langue langue = langueRepository.findById(tradDto.getLangueId())
                        .orElseThrow(() -> new ResourceNotFoundException("Langue", "id", tradDto.getLangueId()));

                // Crée la traduction
                Traduction traduction = new Traduction();
                traduction.setTexte(tradDto.getTexte());  // "Pomme"
                traduction.setLangue(langue);  // Français
                traduction.setFlashcard(flashcard);  // Lie à la flashcard

                // 4. Crée les exemples (si présents)
                if (tradDto.getExemples() != null) {
                    List<Exemple> exemples = new ArrayList<>();

                    for (ExempleDto exDto : tradDto.getExemples()) {  // Boucle sur chaque exemple
                        Exemple exemple = new Exemple();
                        exemple.setPhraseOriginal(exDto.getPhraseOriginal());  // "I eat an apple"
                        exemple.setPhraseTraduite(exDto.getPhraseTraduite());  // "Je mange une pomme"
                        exemple.setTraduction(traduction);  // Lie à la traduction
                        exemples.add(exemple);
                    }

                    traduction.setExemples(exemples);  // Ajoute les exemples à la traduction
                }

                traductions.add(traduction);
            }

            flashcard.setTraductions(traductions);  // Ajoute les traductions à la flashcard
        }

        // 5. Sauvegarde TOUT en cascade (flashcard + traductions + exemples)
        Flashcard savedFlashcard = flashcardRepository.save(flashcard);  // Cascade sauvegarde tout
        return flashcardMapper.toDto(savedFlashcard);  // Renvoie la structure complète
    }

    @Transactional
    public void deleteFlashcard(Long id) {
        if (!flashcardRepository.existsById(id)) {  // Vérifie existence
            throw new ResourceNotFoundException("Flashcard", "id", id);
        }
        flashcardRepository.deleteById(id);  // CASCADE supprime aussi traductions et exemples
    }
}

// Service complexe : gère la création de structures imbriquées (Flashcard → Traduction → Exemple)
// createFlashcard() crée TOUTE la structure en une seule transaction grâce à cascade = CascadeType.ALL
// Structure : Flashcard ("Apple") → Traduction ("Pomme", FR) → Exemple ("I eat an apple" / "Je mange une pomme")
// Une seule sauvegarde (flashcardRepository.save) déclenche l'insertion de tout grâce au cascade