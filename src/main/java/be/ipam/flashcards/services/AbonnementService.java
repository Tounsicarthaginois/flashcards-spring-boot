package be.ipam.flashcards.services;

import be.ipam.flashcards.dto.AbonnementDto;
import be.ipam.flashcards.exception.ResourceNotFoundException;
import be.ipam.flashcards.models.Abonnement;
import be.ipam.flashcards.models.Deck;
import be.ipam.flashcards.models.Utilisateur;
import be.ipam.flashcards.repositories.AbonnementRepository;
import be.ipam.flashcards.repositories.DeckRepository;
import be.ipam.flashcards.repositories.UtilisateurRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Service pour gérer les abonnements
 */
@Service
public class AbonnementService {

    private final AbonnementRepository abonnementRepository;
    private final DeckRepository deckRepository;
    private final UtilisateurRepository utilisateurRepository;

    public AbonnementService(
            AbonnementRepository abonnementRepository,
            DeckRepository deckRepository,
            UtilisateurRepository utilisateurRepository
    ) {
        this.abonnementRepository = abonnementRepository;
        this.deckRepository = deckRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    // Récupère l'utilisateur connecté
    private Utilisateur getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "email", email));
    }

    // S'abonner à un deck
    @Transactional
    public AbonnementDto abonner(Long deckId) {
        Utilisateur user = getCurrentUser();

        // Vérifie si le deck existe
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new ResourceNotFoundException("Deck", "id", deckId));

        // Vérifie si déjà abonné
        if (abonnementRepository.existsByUtilisateurIdAndDeckId(user.getId(), deckId)) {
            throw new IllegalArgumentException("Vous êtes déjà abonné à ce deck");
        }

        // Crée l'abonnement
        Abonnement abonnement = new Abonnement();
        abonnement.setUtilisateur(user);
        abonnement.setDeck(deck);
        abonnement.setNouveauxMotsParJour(10); // Valeur par défaut

        Abonnement saved = abonnementRepository.save(abonnement);
        return toDto(saved);
    }

    // Se désabonner d'un deck
    @Transactional
    public void desabonner(Long deckId) {
        Utilisateur user = getCurrentUser();

        Abonnement abonnement = abonnementRepository
                .findByUtilisateurIdAndDeckId(user.getId(), deckId)
                .orElseThrow(() -> new ResourceNotFoundException("Abonnement", "deckId", deckId));

        abonnementRepository.delete(abonnement);
    }

    // Récupère les abonnements de l'utilisateur
    public List<AbonnementDto> getMesAbonnements() {
        Utilisateur user = getCurrentUser();
        List<Abonnement> abonnements = abonnementRepository.findByUtilisateurId(user.getId());

        List<AbonnementDto> dtos = new ArrayList<>();
        for (Abonnement abo : abonnements) {
            dtos.add(toDto(abo));
        }
        return dtos;
    }

    // Met à jour le nombre de nouveaux mots par jour
    @Transactional
    public AbonnementDto updateNouveauxMotsParJour(Long deckId, Integer nouveauxMots) {
        Utilisateur user = getCurrentUser();

        Abonnement abonnement = abonnementRepository
                .findByUtilisateurIdAndDeckId(user.getId(), deckId)
                .orElseThrow(() -> new ResourceNotFoundException("Abonnement", "deckId", deckId));

        abonnement.setNouveauxMotsParJour(nouveauxMots);
        Abonnement updated = abonnementRepository.save(abonnement);
        return toDto(updated);
    }

    // Convertit une entité en DTO
    private AbonnementDto toDto(Abonnement abonnement) {
        AbonnementDto dto = new AbonnementDto();
        dto.setId(abonnement.getId());
        dto.setUtilisateurId(abonnement.getUtilisateur().getId());
        dto.setDeckId(abonnement.getDeck().getId());
        dto.setDeckNom(abonnement.getDeck().getName());
        dto.setNouveauxMotsParJour(abonnement.getNouveauxMotsParJour());
        dto.setDateAbonnement(abonnement.getDateAbonnement());
        return dto;
    }
}
