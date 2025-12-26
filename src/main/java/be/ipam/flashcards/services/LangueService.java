package be.ipam.flashcards.services;

import be.ipam.flashcards.dto.LangueDto;
import be.ipam.flashcards.exception.ResourceNotFoundException;
import be.ipam.flashcards.mappers.LangueMapper;
import be.ipam.flashcards.models.Langue;
import be.ipam.flashcards.repositories.LangueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service pour les langues
 */
@Service
public class LangueService {

    private final LangueRepository langueRepository;
    private final LangueMapper langueMapper;

    public LangueService(LangueRepository langueRepository, LangueMapper langueMapper) {
        this.langueRepository = langueRepository;
        this.langueMapper = langueMapper;
    }

    public List<LangueDto> getAllLangues() {
        List<Langue> langues = langueRepository.findAll();
        return langueMapper.toDtoList(langues);
    }

    public LangueDto getLangueById(Long id) {
        Langue langue = langueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Langue", "id", id));
        return langueMapper.toDto(langue);
    }

    public LangueDto getLangueByCode(String code) {
        Langue langue = langueRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Langue", "code", code));
        return langueMapper.toDto(langue);
    }

    @Transactional
    public LangueDto createLangue(LangueDto langueDto) {
        if (langueRepository.existsByCode(langueDto.getCode())) {
            throw new IllegalArgumentException("Une langue avec ce code existe déjà : " + langueDto.getCode());
        }

        if (langueRepository.existsByNom(langueDto.getNom())) {
            throw new IllegalArgumentException("Une langue avec ce nom existe déjà : " + langueDto.getNom());
        }

        Langue langue = langueMapper.toEntity(langueDto);
        Langue savedLangue = langueRepository.save(langue);
        return langueMapper.toDto(savedLangue);
    }

    @Transactional
    public LangueDto updateLangue(Long id, LangueDto langueDto) {
        Langue existingLangue = langueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Langue", "id", id));

        existingLangue.setNom(langueDto.getNom());
        existingLangue.setCode(langueDto.getCode());

        Langue updatedLangue = langueRepository.save(existingLangue);
        return langueMapper.toDto(updatedLangue);
    }

    @Transactional
    public void deleteLangue(Long id) {
        if (!langueRepository.existsById(id)) {
            throw new ResourceNotFoundException("Langue", "id", id);
        }
        langueRepository.deleteById(id);
    }
}
