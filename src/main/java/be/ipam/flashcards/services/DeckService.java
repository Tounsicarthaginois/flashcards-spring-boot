package be.ipam.flashcards.services;

import be.ipam.flashcards.dto.DeckDto;
import be.ipam.flashcards.exception.ResourceNotFoundException;
import be.ipam.flashcards.mappers.DeckMapper;
import be.ipam.flashcards.models.Deck;
import be.ipam.flashcards.models.Utilisateur;
import be.ipam.flashcards.repositories.DeckRepository;
import be.ipam.flashcards.repositories.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeckService {

    private final DeckRepository deckRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final DeckMapper deckMapper;

    public DeckService(DeckRepository deckRepository,
                       UtilisateurRepository utilisateurRepository,
                       DeckMapper deckMapper) {
        this.deckRepository = deckRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.deckMapper = deckMapper;
    }

    public List<DeckDto> getAllDecksByUser(Long userId) {
        List<Deck> decks = deckRepository.findByUserId(userId);
        return decks.stream()
                .map(deckMapper::toDto)
                .collect(Collectors.toList());
    }

    public DeckDto getDeckById(Long id, Long userId) {
        Deck deck = deckRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deck", "id", id));
        checkDeckOwnership(deck, userId);
        return deckMapper.toDto(deck);
    }

    @Transactional
    public DeckDto createDeck(DeckDto deckDto, Long userId) {
        if (deckDto.getName() == null || deckDto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du deck est obligatoire");
        }

        Utilisateur user = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "id", userId));

        if (deckRepository.existsByNameAndUserId(deckDto.getName(), userId)) {
            throw new IllegalArgumentException("Vous avez déjà un deck avec le nom : " + deckDto.getName());
        }

        Deck deck = deckMapper.toEntity(deckDto);
        deck.setUser(user);

        Deck savedDeck = deckRepository.save(deck);
        return deckMapper.toDto(savedDeck);
    }

    @Transactional
    public DeckDto updateDeck(Long id, DeckDto deckDto, Long userId) {
        Deck existingDeck = deckRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deck", "id", id));

        checkDeckOwnership(existingDeck, userId);

        if (deckDto.getName() == null || deckDto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du deck est obligatoire");
        }

        if (!existingDeck.getName().equals(deckDto.getName()) &&
                deckRepository.existsByNameAndUserId(deckDto.getName(), userId)) {
            throw new IllegalArgumentException("Vous avez déjà un deck avec le nom : " + deckDto.getName());
        }

        existingDeck.setName(deckDto.getName());
        existingDeck.setDescription(deckDto.getDescription());
        existingDeck.setIsPublic(deckDto.getIsPublic());  // ← LIGNE CRITIQUE

        Deck updatedDeck = deckRepository.save(existingDeck);
        return deckMapper.toDto(updatedDeck);
    }

    @Transactional
    public void deleteDeck(Long id, Long userId) {
        Deck deck = deckRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deck", "id", id));

        checkDeckOwnership(deck, userId);
        deckRepository.deleteById(id);
    }

    public List<DeckDto> searchDecksByName(String name, Long userId) {
        List<Deck> decks = deckRepository.findByNameContainingIgnoreCase(name);
        return decks.stream()
                .map(deckMapper::toDto)
                .collect(Collectors.toList());
    }

    public long countDecksByUser(Long userId) {
        return deckRepository.countByUserId(userId);
    }

    private void checkDeckOwnership(Deck deck, Long userId) {
        if (!deck.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Deck", "id", deck.getId());
        }
    }
}