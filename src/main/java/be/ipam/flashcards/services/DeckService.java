package be.ipam.flashcards.services;

import be.ipam.flashcards.dto.DeckDto;
import be.ipam.flashcards.enums.TypeListe;
import be.ipam.flashcards.exception.ResourceNotFoundException;
import be.ipam.flashcards.mappers.DeckMapper;
import be.ipam.flashcards.models.Deck;
import be.ipam.flashcards.models.Langue;
import be.ipam.flashcards.models.Utilisateur;
import be.ipam.flashcards.repositories.DeckRepository;
import be.ipam.flashcards.repositories.LangueRepository;
import be.ipam.flashcards.repositories.UtilisateurRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service pour gérer les decks
 */
@Service
public class DeckService {

    private final DeckRepository deckRepository;
    private final LangueRepository langueRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final DeckMapper deckMapper;

    public DeckService(DeckRepository deckRepository, LangueRepository langueRepository,
                       UtilisateurRepository utilisateurRepository, DeckMapper deckMapper) {
        this.deckRepository = deckRepository;
        this.langueRepository = langueRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.deckMapper = deckMapper;
    }

    // Récupère l'utilisateur connecté
    private Utilisateur getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "email", email));
    }

    // Liste tous les decks de l'utilisateur connecté
    public List<DeckDto> getAllDecks() {
        Utilisateur currentUser = getCurrentUser();
        List<Deck> decks = deckRepository.findByUserId(currentUser.getId());
        return deckMapper.toDtoList(decks);
    }

    // Récupère un deck par ID
    public DeckDto getDeckById(Long id) {
        Deck deck = deckRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deck", "id", id));
        return deckMapper.toDto(deck);
    }

    // Crée un nouveau deck
    @Transactional
    public DeckDto createDeck(DeckDto deckDto) {
        Utilisateur currentUser = getCurrentUser();

        // Récupère la langue
        Langue langue = langueRepository.findById(deckDto.getLangueId())
                .orElseThrow(() -> new ResourceNotFoundException("Langue", "id", deckDto.getLangueId()));

        // Crée le deck
        Deck deck = deckMapper.toEntity(deckDto);
        deck.setLangue(langue);
        deck.setUser(currentUser);

        // Si le type est null, par défaut PRIVEE
        if (deck.getType() == null) {
            deck.setType(TypeListe.PRIVEE);
        }

        Deck savedDeck = deckRepository.save(deck);
        return deckMapper.toDto(savedDeck);
    }

    // Met à jour un deck
    @Transactional
    public DeckDto updateDeck(Long id, DeckDto deckDto) {
        Utilisateur currentUser = getCurrentUser();

        Deck existingDeck = deckRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deck", "id", id));

        // Vérifie que l'utilisateur est le propriétaire
        if (!existingDeck.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Vous n'êtes pas autorisé à modifier ce deck");
        }

        // Met à jour les champs
        existingDeck.setName(deckDto.getName());
        existingDeck.setDescription(deckDto.getDescription());

        // Met à jour la langue si changée
        if (deckDto.getLangueId() != null && !deckDto.getLangueId().equals(existingDeck.getLangue().getId())) {
            Langue langue = langueRepository.findById(deckDto.getLangueId())
                    .orElseThrow(() -> new ResourceNotFoundException("Langue", "id", deckDto.getLangueId()));
            existingDeck.setLangue(langue);
        }

        Deck updatedDeck = deckRepository.save(existingDeck);
        return deckMapper.toDto(updatedDeck);
    }

    // Supprime un deck
    @Transactional
    public void deleteDeck(Long id) {
        Utilisateur currentUser = getCurrentUser();

        Deck deck = deckRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deck", "id", id));

        // Vérifie que l'utilisateur est le propriétaire
        if (!deck.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Vous n'êtes pas autorisé à supprimer ce deck");
        }

        deckRepository.delete(deck);
    }

    // Recherche des decks par nom
    public List<DeckDto> searchDecksByName(String name) {
        Utilisateur currentUser = getCurrentUser();
        List<Deck> decks = deckRepository.findByUserIdAndNameContaining(currentUser.getId(), name);
        return deckMapper.toDtoList(decks);
    }
}