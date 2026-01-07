package be.ipam.flashcards.security;

import be.ipam.flashcards.models.Utilisateur;
import be.ipam.flashcards.repositories.UtilisateurRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service  // Bean Spring géré automatiquement
public class CustomUserDetailsService implements UserDetailsService {  // Interface requise par Spring Security

    private final UtilisateurRepository utilisateurRepository;

    public CustomUserDetailsService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override  // Méthode appelée par Spring Security pour charger un utilisateur
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {  // "username" = email dans notre cas
        // Récupère l'utilisateur depuis la DB
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'email : " + email));

        // Convertit notre entité Utilisateur en objet UserDetails (format Spring Security)
        return new User(  // User de Spring Security (pas notre entité)
                utilisateur.getEmail(),  // Username
                utilisateur.getPassword(),  // Password crypté BCrypt
                getAuthorities(utilisateur)  // Rôles/permissions
        );
    }

    // Convertit le rôle de l'utilisateur en format Spring Security
    private Collection<? extends GrantedAuthority> getAuthorities(Utilisateur utilisateur) {
        // Spring Security nécessite le préfixe "ROLE_" devant chaque rôle
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + utilisateur.getRole().name()));
        // Exemple : Role.USER devient "ROLE_USER", Role.GESTIONNAIRE devient "ROLE_GESTIONNAIRE"
    }
}

// Service utilisé par Spring Security pour authentifier un utilisateur
// Appelé automatiquement lors du login et par JwtAuthenticationFilter après validation du token
// Transforme notre Utilisateur (entité DB) en UserDetails (format Spring Security)