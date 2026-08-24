package be.ipam.flashcards.mappers;

import be.ipam.flashcards.dto.LangueDto;
import be.ipam.flashcards.models.Langue;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LangueMapper {

    // Convertit Langue (DB) → LangueDto (API)
    public LangueDto toDto(Langue langue) {
        if (langue == null) {
            return null;
        }

        LangueDto dto = new LangueDto();
        dto.setId(langue.getId());
        dto.setNom(langue.getNom());  // "Français"
        dto.setCode(langue.getCode());  // "fr"

        return dto;
    }

    // Convertit LangueDto (API) → Langue (DB)
    public Langue toEntity(LangueDto dto) {
        if (dto == null) {
            return null;
        }

        Langue langue = new Langue();
        langue.setNom(dto.getNom());
        langue.setCode(dto.getCode());

        return langue;
    }

    // Convertit une liste de langues
    public List<LangueDto> toDtoList(List<Langue> langues) {
        if (langues == null) {
            return null;
        }
        return langues.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}

// Mapper simple sans dépendances (pas de relations imbriquées)
// Utilisé par LangueService et aussi par d'autres mappers (DeckMapper, TraductionMapper)