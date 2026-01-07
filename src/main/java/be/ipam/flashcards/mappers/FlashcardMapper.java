package be.ipam.flashcards.mappers;

import be.ipam.flashcards.dto.FlashcardDto;
import be.ipam.flashcards.models.Flashcard;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class FlashcardMapper {

    private final TraductionMapper traductionMapper;  // Injecté pour mapper les traductions imbriquées

    public FlashcardMapper(TraductionMapper traductionMapper) {  // Injection par constructeur
        this.traductionMapper = traductionMapper;
    }

    // Convertit Flashcard (DB) → FlashcardDto (API) avec traductions complètes
    public FlashcardDto toDto(Flashcard flashcard) {
        if (flashcard == null) {
            return null;
        }

        FlashcardDto dto = new FlashcardDto();
        dto.setId(flashcard.getId());
        dto.setQuestion(flashcard.getQuestion());  // "Apple"
        dto.setDeckId(flashcard.getDeck() != null ? flashcard.getDeck().getId() : null);  // ID du deck parent
        dto.setCreatedAt(flashcard.getCreatedAt());
        dto.setTraductions(traductionMapper.toDtoList(flashcard.getTraductions()));  // Délègue au TraductionMapper

        return dto;
    }

    // Convertit FlashcardDto (API) → Flashcard (DB)
    public Flashcard toEntity(FlashcardDto dto) {
        if (dto == null) {
            return null;
        }

        Flashcard flashcard = new Flashcard();
        flashcard.setQuestion(dto.getQuestion());
        // Note : deck et traductions sont gérés dans le Service (relations complexes)

        return flashcard;
    }

    // Convertit une liste de flashcards
    public List<FlashcardDto> toDtoList(List<Flashcard> flashcards) {
        if (flashcards == null) {
            return null;
        }
        return flashcards.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}

// Mapper avec dépendance (TraductionMapper) pour gérer la structure imbriquée
// toDto() convertit Flashcard → FlashcardDto → List<TraductionDto> → List<ExempleDto>