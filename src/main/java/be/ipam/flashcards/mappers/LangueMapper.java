package be.ipam.flashcards.mappers;

import be.ipam.flashcards.dto.LangueDto;
import be.ipam.flashcards.models.Langue;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper pour les langues
 */
@Component
public class LangueMapper {

    public LangueDto toDto(Langue langue) {
        if (langue == null) {
            return null;
        }

        LangueDto dto = new LangueDto();
        dto.setId(langue.getId());
        dto.setNom(langue.getNom());
        dto.setCode(langue.getCode());

        return dto;
    }

    public Langue toEntity(LangueDto dto) {
        if (dto == null) {
            return null;
        }

        Langue langue = new Langue();
        langue.setNom(dto.getNom());
        langue.setCode(dto.getCode());

        return langue;
    }

    public List<LangueDto> toDtoList(List<Langue> langues) {
        if (langues == null) {
            return null;
        }
        return langues.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
