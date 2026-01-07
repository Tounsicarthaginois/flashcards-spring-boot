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

@Service
public class DeckService {

    private final DeckRepository deckRepository;
    private final LangueRepository langueRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final DeckMapper deckMapper;  // Pour convertir Entity ↔ DTO

    public DeckService(DeckRepository deckRepository, LangueRepository langueRepository,
                       UtilisateurRepository utilisateurRepository, DeckMapper deckMapper) {
        this.deckRepository = deckRepository;
        this.langueRepository = langueRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.deckMapper = deckMapper;
    }

    // Récupère l'utilisateur actuellement connecté (via JWT)
    private Utilisateur getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();  // Récupère du SecurityContext
        String email = auth.getName();  // Email stocké dans le token JWT
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "email", email));
    }

    // Liste tous MES decks (utilisateur connecté)
    public List<DeckDto> getAllDecks() {
        Utilisateur currentUser = getCurrentUser();
        List<Deck> decks = deckRepository.findByUserId(currentUser.getId());  // SELECT WHERE user_id = ?
        return deckMapper.toDtoList(decks);  // Convertit List<Deck> → List<DeckDto>
    }

    // Récupère UN deck par son ID
    public DeckDto getDeckById(Long id) {
        Deck deck = deckRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deck", "id", id));
        return deckMapper.toDto(deck);  // Convertit Deck → DeckDto
    }

    @Transactional  // Transaction DB
    public DeckDto createDeck(DeckDto deckDto) {
        Utilisateur currentUser = getCurrentUser();  // Récupère l'user connecté

        // Vérifie que la langue existe
        Langue langue = langueRepository.findById(deckDto.getLangueId())
                .orElseThrow(() -> new ResourceNotFoundException("Langue", "id", deckDto.getLangueId()));

        // Convertit DTO → Entity
        Deck deck = deckMapper.toEntity(deckDto);
        deck.setLangue(langue);  // Définit la relation @ManyToOne
        deck.setUser(currentUser);  // Définit le créateur (user connecté)

        // Type par défaut : PRIVEE
        if (deck.getType() == null) {
            deck.setType(TypeListe.PRIVEE);
        }

        Deck savedDeck = deckRepository.save(deck);  // INSERT INTO decks
        return deckMapper.toDto(savedDeck);  // Renvoie le DTO
    }

    @Transactional
    public DeckDto updateDeck(Long id, DeckDto deckDto) {
        Utilisateur currentUser = getCurrentUser();

        // Récupère le deck existant
        Deck existingDeck = deckRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deck", "id", id));

        // Vérifie que c'est bien SON deck (sécurité)
        if (!existingDeck.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Vous n'êtes pas autorisé à modifier ce deck");
        }

        // Met à jour les champs simples
        existingDeck.setName(deckDto.getName());
        existingDeck.setDescription(deckDto.getDescription());

        // Met à jour la langue si elle a changé
        if (deckDto.getLangueId() != null && !deckDto.getLangueId().equals(existingDeck.getLangue().getId())) {
            Langue langue = langueRepository.findById(deckDto.getLangueId())
                    .orElseThrow(() -> new ResourceNotFoundException("Langue", "id", deckDto.getLangueId()));
            existingDeck.setLangue(langue);
        }

        Deck updatedDeck = deckRepository.save(existingDeck);  // UPDATE decks
        return deckMapper.toDto(updatedDeck);
    }

    @Transactional
    public void deleteDeck(Long id) {
        Utilisateur currentUser = getCurrentUser();

        Deck deck = deckRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deck", "id", id));

        // Vérifie que c'est bien SON deck
        if (!deck.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Vous n'êtes pas autorisé à supprimer ce deck");
        }

        deckRepository.delete(deck);  // DELETE FROM decks (+ cascade = supprime flashcards aussi)
    }

    // Recherche des decks par nom (LIKE %name%)
    public List<DeckDto> searchDecksByName(String name) {
        Utilisateur currentUser = getCurrentUser();
        List<Deck> decks = deckRepository.findByUserIdAndNameContaining(currentUser.getId(), name);  // WHERE name LIKE %?%
        return deckMapper.toDtoList(decks);
    }
}

// Service pour gérer les decks (listes de flashcards)
// Toujours vérifie que l'utilisateur est propriétaire avant modification/suppression (sécurité)
// getCurrentUser() récupère l'user connecté via le JWT (mis dans SecurityContext par JwtAuthenticationFilter)