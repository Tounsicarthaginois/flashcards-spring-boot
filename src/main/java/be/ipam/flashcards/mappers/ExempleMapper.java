package be.ipam.flashcards.mappers;

import be.ipam.flashcards.dto.ExempleDto;
import be.ipam.flashcards.models.Exemple;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper pour les Exemples
 */
@Component
public class ExempleMapper {

    public ExempleDto toDto(Exemple exemple) {
        if (exemple == null) {
            return null;
        }

        ExempleDto dto = new ExempleDto();
        dto.setId(exemple.getId());
        dto.setPhraseOriginal(exemple.getPhraseOriginal());
        dto.setPhraseTraduite(exemple.getPhraseTraduite());

        return dto;
    }

    public Exemple toEntity(ExempleDto dto) {
        if (dto == null) {
            return null;
        }

        Exemple exemple = new Exemple();
        exemple.setPhraseOriginal(dto.getPhraseOriginal());
        exemple.setPhraseTraduite(dto.getPhraseTraduite());

        return exemple;
    }

    public List<ExempleDto> toDtoList(List<Exemple> exemples) {
        if (exemples == null) {
            return null;
        }
        return exemples.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
