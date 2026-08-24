package be.ipam.flashcards.mappers;

import be.ipam.flashcards.dto.ExempleDto;
import be.ipam.flashcards.models.Exemple;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component  // Bean Spring
public class ExempleMapper {

    // Convertit Exemple (DB) → ExempleDto (API)
    public ExempleDto toDto(Exemple exemple) {
        if (exemple == null) {
            return null;
        }

        ExempleDto dto = new ExempleDto();
        dto.setId(exemple.getId());
        dto.setPhraseOriginal(exemple.getPhraseOriginal());  // "I eat an apple"
        dto.setPhraseTraduite(exemple.getPhraseTraduite());  // "Je mange une pomme"

        return dto;
    }

    // Convertit ExempleDto (API) → Exemple (DB)
    public Exemple toEntity(ExempleDto dto) {
        if (dto == null) {
            return null;
        }

        Exemple exemple = new Exemple();
        exemple.setPhraseOriginal(dto.getPhraseOriginal());
        exemple.setPhraseTraduite(dto.getPhraseTraduite());
        // Note : la relation @ManyToOne vers Traduction est définie dans le Service

        return exemple;
    }

    // Convertit une liste d'exemples
    public List<ExempleDto> toDtoList(List<Exemple> exemples) {
        if (exemples == null) {
            return null;
        }
        return exemples.stream()
                .map(this::toDto)  // Applique toDto() à chaque élément
                .collect(Collectors.toList());
    }
}

// Mapper simple pour les exemples (phrases d'utilisation)
// Utilisé par TraductionMapper dans la conversion imbriquée