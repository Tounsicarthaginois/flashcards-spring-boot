package be.ipam.flashcards.mappers;

import be.ipam.flashcards.dto.TraductionDto;
import be.ipam.flashcards.models.Traduction;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper pour les Traductions
 */
@Component
public class TraductionMapper {

    private final ExempleMapper exempleMapper;

    public TraductionMapper(ExempleMapper exempleMapper) {
        this.exempleMapper = exempleMapper;
    }

    public TraductionDto toDto(Traduction traduction) {
        if (traduction == null) {
            return null;
        }

        TraductionDto dto = new TraductionDto();
        dto.setId(traduction.getId());
        dto.setTexte(traduction.getTexte());
        dto.setLangueId(traduction.getLangue() != null ? traduction.getLangue().getId() : null);
        dto.setLangueNom(traduction.getLangue() != null ? traduction.getLangue().getNom() : null);
        dto.setExemples(exempleMapper.toDtoList(traduction.getExemples()));

        return dto;
    }

    public Traduction toEntity(TraductionDto dto) {
        if (dto == null) {
            return null;
        }

        Traduction traduction = new Traduction();
        traduction.setTexte(dto.getTexte());

        return traduction;
    }

    public List<TraductionDto> toDtoList(List<Traduction> traductions) {
        if (traductions == null) {
            return null;
        }
        return traductions.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}