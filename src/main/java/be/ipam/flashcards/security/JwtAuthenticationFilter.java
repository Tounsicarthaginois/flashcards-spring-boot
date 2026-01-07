package be.ipam.flashcards.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component  // Bean Spring
public class JwtAuthenticationFilter extends OncePerRequestFilter {  // S'exécute une fois par requête

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override  // Méthode appelée AVANT chaque requête HTTP
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,  // Requête HTTP entrante
            @NonNull HttpServletResponse response,  // Réponse HTTP sortante
            @NonNull FilterChain filterChain  // Chaîne de filtres suivants
    ) throws ServletException, IOException {

        // 1. Récupère le header Authorization de la requête
        final String authHeader = request.getHeader("Authorization");  // Ex: "Bearer eyJhbGci..."
        final String jwt;
        final String userEmail;

        // 2. Vérifie que le header existe et commence par "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);  // Pas de token → continue sans authentifier
            return;
        }

        // 3. Extrait le token JWT (enlève "Bearer " du début)
        jwt = authHeader.substring(7).trim();  // substring(7) enlève "Bearer " (7 caractères)

        try {
            // 4. Extrait l'email depuis le token JWT
            userEmail = jwtService.extractEmail(jwt);  // Décode le token et lit le "subject"

            // 5. Si email valide ET utilisateur pas encore authentifié dans cette requête
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 6. Charge l'utilisateur complet depuis la DB
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                // 7. Vérifie que le token est valide (signature + expiration)
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    // 8. Crée un objet d'authentification Spring Security
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,  // Principal (l'utilisateur)
                            null,  // Credentials (pas besoin, déjà authentifié par token)
                            userDetails.getAuthorities()  // Rôles (ROLE_USER, ROLE_GESTIONNAIRE)
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 9. Met l'utilisateur dans le SecurityContext (= "connecté" pour cette requête)
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {  // Si token invalide/expiré/corrompu
            System.err.println("Erreur JWT: " + e.getMessage());
            // Continue sans authentifier (le controller renverra 401 si route protégée)
        }

        // 10. Continue vers le prochain filtre ou le controller
        filterChain.doFilter(request, response);
    }
}

// Filtre s'exécutant AVANT chaque requête pour vérifier le token JWT
// Flow : Header → Extraction token → Validation → Chargement user → Mise dans SecurityContext
// Après ce filtre, SecurityContextHolder.getContext().getAuthentication() contient l'utilisateur connecté