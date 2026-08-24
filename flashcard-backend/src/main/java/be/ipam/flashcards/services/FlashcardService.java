package be.ipam.flashcards.services;

import be.ipam.flashcards.dto.ExempleDto;
import be.ipam.flashcards.dto.FlashcardDto;
import be.ipam.flashcards.dto.TraductionDto;
import be.ipam.flashcards.enums.Role;
import be.ipam.flashcards.exception.ResourceNotFoundException;
import be.ipam.flashcards.mappers.FlashcardMapper;
import be.ipam.flashcards.models.*;
import be.ipam.flashcards.repositories.*;
import org.springframework.security.access.AccessDeniedException;
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

    // On réutilise la règle d'accès écrite dans DeckService plutôt que de
    // la recopier ici : une seule définition, donc pas de risque que les
    // deux versions divergent un jour.
    private final DeckService deckService;

    public FlashcardService(
            FlashcardRepository flashcardRepository,
            DeckRepository deckRepository,
            LangueRepository langueRepository,
            UtilisateurRepository utilisateurRepository,
            FlashcardMapper flashcardMapper,
            DeckService deckService
    ) {
        this.flashcardRepository = flashcardRepository;
        this.deckRepository = deckRepository;
        this.langueRepository = langueRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.flashcardMapper = flashcardMapper;
        this.deckService = deckService;
    }

    // Récupère l'utilisateur connecté via le token JWT
    private Utilisateur getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "email", email));
    }

    // Récupère toutes les flashcards d'un deck.
    // Protéger le deck sans protéger ses cartes ne servirait à rien :
    // il suffirait d'appeler cette route pour lire quand même le contenu.
    // On applique donc exactement la même règle qu'à l'ouverture du deck.
    public List<FlashcardDto> getFlashcardsByDeckId(Long deckId) {
        Utilisateur currentUser = getCurrentUser();

        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new ResourceNotFoundException("Deck", "id", deckId));

        if (!deckService.peutConsulter(deck, currentUser)) {
            throw new AccessDeniedException("Vous n'êtes pas autorisé à consulter ce deck");
        }

        List<Flashcard> flashcards = flashcardRepository.findByDeckId(deckId);
        return flashcardMapper.toDtoList(flashcards);
    }

    // Récupère une flashcard par ID.
    // Le droit dépend du deck auquel elle appartient, pas de la carte
    // elle-même : c'est le deck qui porte la notion de privé ou public.
    public FlashcardDto getFlashcardById(Long id) {
        Utilisateur currentUser = getCurrentUser();

        Flashcard flashcard = flashcardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flashcard", "id", id));

        if (!deckService.peutConsulter(flashcard.getDeck(), currentUser)) {
            throw new AccessDeniedException("Vous n'êtes pas autorisé à consulter cette flashcard");
        }

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