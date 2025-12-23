package be.ipam.flashcards.mappers;

import be.ipam.flashcards.dto.FlashcardDto;
import be.ipam.flashcards.models.Flashcard;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * MAPPER FLASHCARD - Conversion manuelle (sans MapStruct)
 */
@Component
public class FlashcardMapper {

    // Convertit Flashcard → FlashcardDto
    public FlashcardDto toDto(Flashcard flashcard) {
        if (flashcard == null) {
            return null;
        }

        FlashcardDto dto = new FlashcardDto();
        dto.setId(flashcard.getId());
        dto.setQuestion(flashcard.getQuestion());
        dto.setAnswer(flashcard.getAnswer());
        dto.setDeckId(flashcard.getDeck() != null ? flashcard.getDeck().getId() : null);
        dto.setCreatedAt(flashcard.getCreatedAt());

        return dto;
    }

    // Convertit liste de Flashcards → liste de FlashcardDtos
    public List<FlashcardDto> toDtoList(List<Flashcard> flashcards) {
        if (flashcards == null) {
            return null;
        }
        return flashcards.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // Convertit FlashcardDto → Flashcard
    public Flashcard toEntity(FlashcardDto dto) {
        if (dto == null) {
            return null;
        }

        Flashcard flashcard = new Flashcard();
        flashcard.setQuestion(dto.getQuestion());
        flashcard.setAnswer(dto.getAnswer());

        return flashcard;
    }
}