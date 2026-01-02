package be.ipam.flashcards.mappers;

import be.ipam.flashcards.dto.FlashcardDto;
import be.ipam.flashcards.models.Flashcard;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper pour les Flashcards
 */
@Component
public class FlashcardMapper {

    private final TraductionMapper traductionMapper;

    public FlashcardMapper(TraductionMapper traductionMapper) {
        this.traductionMapper = traductionMapper;
    }

    public FlashcardDto toDto(Flashcard flashcard) {
        if (flashcard == null) {
            return null;
        }

        FlashcardDto dto = new FlashcardDto();
        dto.setId(flashcard.getId());
        dto.setQuestion(flashcard.getQuestion());
        dto.setDeckId(flashcard.getDeck() != null ? flashcard.getDeck().getId() : null);
        dto.setCreatedAt(flashcard.getCreatedAt());
        dto.setTraductions(traductionMapper.toDtoList(flashcard.getTraductions()));

        return dto;
    }

    public Flashcard toEntity(FlashcardDto dto) {
        if (dto == null) {
            return null;
        }

        Flashcard flashcard = new Flashcard();
        flashcard.setQuestion(dto.getQuestion());

        return flashcard;
    }

    public List<FlashcardDto> toDtoList(List<Flashcard> flashcards) {
        if (flashcards == null) {
            return null;
        }
        return flashcards.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}