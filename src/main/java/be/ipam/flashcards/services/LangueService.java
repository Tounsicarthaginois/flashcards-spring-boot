package be.ipam.flashcards.services;

import be.ipam.flashcards.dto.LangueDto;
import be.ipam.flashcards.exception.ResourceNotFoundException;
import be.ipam.flashcards.mappers.LangueMapper;
import be.ipam.flashcards.models.Langue;
import be.ipam.flashcards.repositories.LangueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LangueService {

    private final LangueRepository langueRepository;
    private final LangueMapper langueMapper;

    public LangueService(LangueRepository langueRepository, LangueMapper langueMapper) {
        this.langueRepository = langueRepository;
        this.langueMapper = langueMapper;
    }

    // Liste toutes les langues disponibles (accessible à tous)
    public List<LangueDto> getAllLangues() {
        List<Langue> langues = langueRepository.findAll();  // SELECT * FROM langues
        return langueMapper.toDtoList(langues);
    }

    // Récupère une langue par son ID
    public LangueDto getLangueById(Long id) {
        Langue langue = langueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Langue", "id", id));
        return langueMapper.toDto(langue);
    }

    // Récupère une langue par son code ("fr", "en", "es"...)
    public LangueDto getLangueByCode(String code) {
        Langue langue = langueRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Langue", "code", code));
        return langueMapper.toDto(langue);
    }

    @Transactional  // PROTÉGÉ par @PreAuthorize("hasRole('GESTIONNAIRE')") dans le controller
    public LangueDto createLangue(LangueDto langueDto) {
        // Vérifie que le code n'existe pas déjà
        if (langueRepository.existsByCode(langueDto.getCode())) {
            throw new IllegalArgumentException("Une langue avec ce code existe déjà : " + langueDto.getCode());
        }

        // Vérifie que le nom n'existe pas déjà
        if (langueRepository.existsByNom(langueDto.getNom())) {
            throw new IllegalArgumentException("Une langue avec ce nom existe déjà : " + langueDto.getNom());
        }

        // Crée la langue
        Langue langue = langueMapper.toEntity(langueDto);
        Langue savedLangue = langueRepository.save(langue);  // INSERT INTO langues
        return langueMapper.toDto(savedLangue);
    }

    @Transactional  // PROTÉGÉ par @PreAuthorize dans le controller
    public LangueDto updateLangue(Long id, LangueDto langueDto) {
        // Récupère la langue existante
        Langue existingLangue = langueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Langue", "id", id));

        // Met à jour les champs
        existingLangue.setNom(langueDto.getNom());  // "Français"
        existingLangue.setCode(langueDto.getCode());  // "fr"

        Langue updatedLangue = langueRepository.save(existingLangue);  // UPDATE langues
        return langueMapper.toDto(updatedLangue);
    }

    @Transactional  // PROTÉGÉ par @PreAuthorize dans le controller
    public void deleteLangue(Long id) {
        if (!langueRepository.existsById(id)) {  // Vérifie existence
            throw new ResourceNotFoundException("Langue", "id", id);
        }
        langueRepository.deleteById(id);  // DELETE FROM langues
    }
}

// Service simple pour gérer les langues disponibles
// Lecture (GET) accessible à tous, mais création/modification/suppression réservée aux GESTIONNAIRE
// Vérifie les doublons avant création (code et nom doivent être uniques)