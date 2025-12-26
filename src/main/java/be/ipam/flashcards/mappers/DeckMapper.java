package be.ipam.flashcards.mappers;

import be.ipam.flashcards.dto.DeckDto;
import be.ipam.flashcards.models.Deck;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DeckMapper {

    public DeckDto toDto(Deck deck) {
        if (deck == null) {
            return null;
        }

        DeckDto dto = new DeckDto();
        dto.setId(deck.getId());
        dto.setName(deck.getName());
        dto.setDescription(deck.getDescription());
        dto.setType(deck.getType());
        dto.setLangueId(deck.getLangue() != null ? deck.getLangue().getId() : null);
        dto.setLangueNom(deck.getLangue() != null ? deck.getLangue().getNom() : null);
        dto.setUserId(deck.getUser() != null ? deck.getUser().getId() : null);
        dto.setValidateurId(deck.getValidateur() != null ? deck.getValidateur().getId() : null);
        dto.setCreatedAt(deck.getCreatedAt());
        dto.setFlashcardCount(deck.getFlashcards() != null ? deck.getFlashcards().size() : 0);

        return dto;
    }

    public Deck toEntity(DeckDto dto) {
        if (dto == null) {
            return null;
        }

        Deck deck = new Deck();
        deck.setName(dto.getName());
        deck.setDescription(dto.getDescription());
        deck.setType(dto.getType());

        return deck;
    }

    public List<DeckDto> toDtoList(List<Deck> decks) {
        if (decks == null) {
            return null;
        }
        return decks.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}