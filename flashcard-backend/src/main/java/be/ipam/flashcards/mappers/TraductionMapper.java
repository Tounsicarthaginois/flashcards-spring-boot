package be.ipam.flashcards.mappers;

import be.ipam.flashcards.dto.TraductionDto;
import be.ipam.flashcards.models.Traduction;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TraductionMapper {

    private final ExempleMapper exempleMapper;  // Injecté pour mapper les exemples imbriqués

    public TraductionMapper(ExempleMapper exempleMapper) {  // Injection par constructeur
        this.exempleMapper = exempleMapper;
    }

    // Convertit Traduction (DB) → TraductionDto (API) avec exemples
    public TraductionDto toDto(Traduction traduction) {
        if (traduction == null) {
            return null;
        }

        TraductionDto dto = new TraductionDto();
        dto.setId(traduction.getId());
        dto.setTexte(traduction.getTexte());  // "Pomme"
        dto.setLangueId(traduction.getLangue() != null ? traduction.getLangue().getId() : null);  // ID de la langue
        dto.setLangueNom(traduction.getLangue() != null ? traduction.getLangue().getNom() : null);  // "Français"
        dto.setExemples(exempleMapper.toDtoList(traduction.getExemples()));  // Délègue au ExempleMapper

        return dto;
    }

    // Convertit TraductionDto (API) → Traduction (DB)
    public Traduction toEntity(TraductionDto dto) {
        if (dto == null) {
            return null;
        }

        Traduction traduction = new Traduction();
        traduction.setTexte(dto.getTexte());
        // Note : langue, flashcard et exemples gérés dans le Service

        return traduction;
    }

    // Convertit une liste de traductions
    public List<TraductionDto> toDtoList(List<Traduction> traductions) {
        if (traductions == null) {
            return null;
        }
        return traductions.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}

// Mapper intermédiaire dans la chaîne : Flashcard → Traduction → Exemple
// Utilisé par FlashcardMapper pour construire la structure complète imbriquée