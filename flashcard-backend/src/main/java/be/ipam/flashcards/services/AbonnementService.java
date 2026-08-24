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

@Service  // Service Spring (logique métier)
public class AbonnementService {

    private final AbonnementRepository abonnementRepository;
    private final DeckRepository deckRepository;
    private final UtilisateurRepository utilisateurRepository;

    public AbonnementService(  // Injection des repositories par constructeur
                               AbonnementRepository abonnementRepository,
                               DeckRepository deckRepository,
                               UtilisateurRepository utilisateurRepository
    ) {
        this.abonnementRepository = abonnementRepository;
        this.deckRepository = deckRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    // Récupère l'utilisateur actuellement connecté via JWT
    private Utilisateur getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();  // Récupère l'authentification
        String email = auth.getName();  // Récupère le "username" = email dans notre cas
        return utilisateurRepository.findByEmail(email)  // Cherche l'utilisateur en DB
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "email", email));
    }

    @Transactional  // Transaction DB (commit si succès, rollback si erreur)
    public AbonnementDto abonner(Long deckId) {
        Utilisateur user = getCurrentUser();  // Récupère l'user connecté

        // Vérifie que le deck existe
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new ResourceNotFoundException("Deck", "id", deckId));

        // Vérifie que l'user n'est pas déjà abonné (évite doublons)
        if (abonnementRepository.existsByUtilisateurIdAndDeckId(user.getId(), deckId)) {
            throw new IllegalArgumentException("Vous êtes déjà abonné à ce deck");
        }

        // Crée l'abonnement
        Abonnement abonnement = new Abonnement();
        abonnement.setUtilisateur(user);
        abonnement.setDeck(deck);
        abonnement.setNouveauxMotsParJour(10);  // Valeur par défaut (paramètre d'apprentissage)

        Abonnement saved = abonnementRepository.save(abonnement);  // INSERT INTO abonnements
        return toDto(saved);  // Convertit en DTO pour renvoyer
    }

    @Transactional
    public void desabonner(Long deckId) {
        Utilisateur user = getCurrentUser();

        // Cherche l'abonnement
        Abonnement abonnement = abonnementRepository
                .findByUtilisateurIdAndDeckId(user.getId(), deckId)
                .orElseThrow(() -> new ResourceNotFoundException("Abonnement", "deckId", deckId));

        abonnementRepository.delete(abonnement);  // DELETE FROM abonnements WHERE id = ?
    }

    // Récupère tous les abonnements de l'utilisateur connecté
    public List<AbonnementDto> getMesAbonnements() {
        Utilisateur user = getCurrentUser();
        List<Abonnement> abonnements = abonnementRepository.findByUtilisateurId(user.getId());  // SELECT par user_id

        // Convertit chaque entité en DTO
        List<AbonnementDto> dtos = new ArrayList<>();
        for (Abonnement abo : abonnements) {
            dtos.add(toDto(abo));
        }
        return dtos;
    }

    @Transactional
    public AbonnementDto updateNouveauxMotsParJour(Long deckId, Integer nouveauxMots) {
        Utilisateur user = getCurrentUser();

        // Récupère l'abonnement
        Abonnement abonnement = abonnementRepository
                .findByUtilisateurIdAndDeckId(user.getId(), deckId)
                .orElseThrow(() -> new ResourceNotFoundException("Abonnement", "deckId", deckId));

        abonnement.setNouveauxMotsParJour(nouveauxMots);  // Modifie le paramètre
        Abonnement updated = abonnementRepository.save(abonnement);  // UPDATE
        return toDto(updated);
    }

    // Convertit Abonnement (entité) → AbonnementDto (pour l'API)
    private AbonnementDto toDto(Abonnement abonnement) {
        AbonnementDto dto = new AbonnementDto();
        dto.setId(abonnement.getId());
        dto.setUtilisateurId(abonnement.getUtilisateur().getId());
        dto.setDeckId(abonnement.getDeck().getId());
        dto.setDeckNom(abonnement.getDeck().getName());  // Pour affichage
        dto.setNouveauxMotsParJour(abonnement.getNouveauxMotsParJour());
        dto.setDateAbonnement(abonnement.getDateAbonnement());
        return dto;
    }
}

// Service pour gérer les abonnements aux decks
// Permet de s'abonner/désabonner et de paramétrer combien de nouveaux mots par jour
// getCurrentUser() récupère l'utilisateur connecté grâce au JWT (mis dans SecurityContext par JwtAuthenticationFilter)