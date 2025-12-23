package be.ipam.flashcards.mappers;

import be.ipam.flashcards.dto.FlashcardDto;
import be.ipam.flashcards.models.Flashcard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * MAPPER FLASHCARD - Convertit entre Flashcard et FlashcardDto
 */
@Mapper(componentModel = "spring")
public interface FlashcardMapper {

    // Convertit Flashcard → FlashcardDto
    @Mapping(source = "deck.id", target = "deckId")
    FlashcardDto toDto(Flashcard flashcard);

    // Convertit liste de Flashcards → liste de FlashcardDtos
    List<FlashcardDto> toDtoList(List<Flashcard> flashcards);

    // Convertit FlashcardDto → Flashcard
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deck", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Flashcard toEntity(FlashcardDto flashcardDto);
}