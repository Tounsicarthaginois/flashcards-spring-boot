package be.ipam.flashcards.mappers;

import be.ipam.flashcards.dto.DeckDto;
import be.ipam.flashcards.models.Deck;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component  // Spring gère cette classe (injection de dépendance)
public class DeckMapper {

    // Convertit une entité Deck (DB) en DeckDto (API)
    public DeckDto toDto(Deck deck) {
        if (deck == null) {  // Sécurité contre null
            return null;
        }

        DeckDto dto = new DeckDto();
        dto.setId(deck.getId());
        dto.setName(deck.getName());
        dto.setDescription(deck.getDescription());
        dto.setType(deck.getType());  // Enum directement copié
        dto.setLangueId(deck.getLangue() != null ? deck.getLangue().getId() : null);  // Juste l'ID de la langue
        dto.setLangueNom(deck.getLangue() != null ? deck.getLangue().getNom() : null);  // Nom pour affichage
        dto.setUserId(deck.getUser() != null ? deck.getUser().getId() : null);  // ID du créateur
        dto.setValidateurId(deck.getValidateur() != null ? deck.getValidateur().getId() : null);  // ID validateur si validé
        dto.setCreatedAt(deck.getCreatedAt());
        dto.setFlashcardCount(deck.getFlashcards() != null ? deck.getFlashcards().size() : 0);  // Compte les flashcards

        return dto;
    }

    // Convertit un DeckDto (API) en entité Deck (DB)
    public Deck toEntity(DeckDto dto) {
        if (dto == null) {
            return null;
        }

        Deck deck = new Deck();
        deck.setName(dto.getName());
        deck.setDescription(dto.getDescription());
        deck.setType(dto.getType());
        // Note : langue, user, validateur sont définis dans le Service, pas ici
        // Le mapper ne gère que les champs simples

        return deck;
    }

    // Convertit une liste d'entités en liste de DTOs
    public List<DeckDto> toDtoList(List<Deck> decks) {
        if (decks == null) {
            return null;
        }
        return decks.stream()  // Stream Java 8
                .map(this::toDto)  // Applique toDto() à chaque élément
                .collect(Collectors.toList());  // Récupère dans une liste
    }
}

// Les mappers séparent les entités (DB) des DTOs (API)
// Avantage : on ne renvoie jamais l'objet Langue complet, juste son ID et nom