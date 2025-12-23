package be.ipam.flashcards.mappers;

import be.ipam.flashcards.dto.DeckDto;
import be.ipam.flashcards.models.Deck;
import org.springframework.stereotype.Component;

/**
 * MAPPER DECK - Conversion manuelle (sans MapStruct)
 */
@Component
public class DeckMapper {

    // Convertit Deck → DeckDto
    public DeckDto toDto(Deck deck) {
        if (deck == null) {
            return null;
        }

        DeckDto dto = new DeckDto();
        dto.setId(deck.getId());
        dto.setName(deck.getName());
        dto.setDescription(deck.getDescription());
        dto.setUserId(deck.getUser() != null ? deck.getUser().getId() : null);
        dto.setIsPublic(deck.getIsPublic());
        dto.setCreatedAt(deck.getCreatedAt());
        dto.setFlashcardCount(deck.getFlashcardCount());
        dto.setFlashcards(null); // Pas de flashcards par défaut

        return dto;
    }

    // Convertit DeckDto → Deck
    public Deck toEntity(DeckDto dto) {
        if (dto == null) {
            return null;
        }

        Deck deck = new Deck();
        deck.setName(dto.getName());
        deck.setDescription(dto.getDescription());
        deck.setIsPublic(dto.getIsPublic() != null ? dto.getIsPublic() : false);

        return deck;
    }
}