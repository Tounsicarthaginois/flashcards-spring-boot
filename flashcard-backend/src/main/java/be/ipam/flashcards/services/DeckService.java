package be.ipam.flashcards.services;

import be.ipam.flashcards.dto.DeckDto;
import be.ipam.flashcards.enums.Role;
import be.ipam.flashcards.enums.TypeListe;
import be.ipam.flashcards.exception.ResourceNotFoundException;
import be.ipam.flashcards.mappers.DeckMapper;
import be.ipam.flashcards.models.Deck;
import be.ipam.flashcards.models.Langue;
import be.ipam.flashcards.models.Utilisateur;
import be.ipam.flashcards.repositories.DeckRepository;
import be.ipam.flashcards.repositories.LangueRepository;
import be.ipam.flashcards.repositories.UtilisateurRepository;
import org.springframework.security.access.AccessDeniedException;
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

    // ============================================================
    // RÈGLE DE LECTURE D'UN DECK — le point central des autorisations
    // ============================================================
    // getAllDecks() filtre déjà par propriétaire, donc personne ne voit
    // le deck d'un autre DANS LA LISTE. Mais ça ne suffit pas : sans le
    // contrôle ci-dessous, n'importe qui pouvait ouvrir /flashcards/7 et
    // lire le deck privé d'un autre en devinant simplement le numéro.
    // (faille classique : référence directe non sécurisée)
    //
    // Trois cas donnent le droit de lire un deck, et trois seulement :
    public boolean peutConsulter(Deck deck, Utilisateur user) {

        // 1. Un deck OFFICIELLE est public : validé par un gestionnaire,
        //    il est destiné à être consulté par tout le monde.
        if (deck.getType() == TypeListe.OFFICIELLE) {
            return true;
        }

        // 2. Le créateur du deck y a toujours accès, quel que soit le type.
        if (deck.getUser().getId().equals(user.getId())) {
            return true;
        }

        // 3. Un GESTIONNAIRE peut lire un deck PUBLIQUE : il doit pouvoir
        //    l'examiner avant de le valider. Volontairement limité au type
        //    PUBLIQUE — un administrateur n'a AUCUN accès aux decks privés.
        if (user.getRole() == Role.GESTIONNAIRE && deck.getType() == TypeListe.PUBLIQUE) {
            return true;
        }

        // Tout le reste est refusé, y compris pour un administrateur.
        return false;
    }

    // Récupère UN deck par son ID
    public DeckDto getDeckById(Long id) {
        Utilisateur currentUser = getCurrentUser();

        Deck deck = deckRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deck", "id", id));

        // Contrôle d'accès : sans cette ligne, l'URL suffisait pour tout lire.
        // AccessDeniedException est traduite en 403 par GlobalExceptionHandler,
        // et le front redirige alors l'utilisateur vers ses propres decks.
        if (!peutConsulter(deck, currentUser)) {
            throw new AccessDeniedException("Vous n'êtes pas autorisé à consulter ce deck");
        }

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
    // Récupère tous les decks PUBLIQUES en attente de validation (GESTIONNAIRE)
    public List<DeckDto> getDecksPubliquesEnAttente() {
        List<Deck> decks = deckRepository.findByType(TypeListe.PUBLIQUE);
        return deckMapper.toDtoList(decks);
    }

    // Valide un deck PUBLIQUE → le passe en OFFICIELLE (GESTIONNAIRE)
    @Transactional
    public DeckDto validerDeck(Long id) {
        Utilisateur validateur = getCurrentUser(); // Le gestionnaire connecté devient le validateur

        Deck deck = deckRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deck", "id", id));

        // Change le type de PUBLIQUE → OFFICIELLE
        deck.setType(TypeListe.OFFICIELLE);
        // Enregistre qui a validé ce deck
        deck.setValidateur(validateur);

        Deck savedDeck = deckRepository.save(deck);
        return deckMapper.toDto(savedDeck);
    }

    // Récupère tous les decks OFFICIELS (visibles par tous les utilisateurs)
    public List<DeckDto> getDecksOfficiels() {
        List<Deck> decks = deckRepository.findByType(TypeListe.OFFICIELLE);
        return deckMapper.toDtoList(decks);
    }
}

// Service pour gérer les decks (listes de flashcards)
// Toujours vérifie que l'utilisateur est propriétaire avant modification/suppression (sécurité)
// getCurrentUser() récupère l'user connecté via le JWT (mis dans SecurityContext par JwtAuthenticationFilter)