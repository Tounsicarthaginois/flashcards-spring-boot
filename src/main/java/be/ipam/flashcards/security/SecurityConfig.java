package be.ipam.flashcards.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration  // Classe de configuration Spring
@EnableWebSecurity  // Active Spring Security
@EnableMethodSecurity  // Active @PreAuthorize sur les méthodes (ex: @PreAuthorize("hasRole('GESTIONNAIRE')"))
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, UserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean  // Définit la chaîne de filtres de sécurité
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)  // Désactive CSRF (pas nécessaire pour API REST stateless)
                .authorizeHttpRequests(auth -> auth
                        // Routes PUBLIQUES (accessibles sans token JWT)
                        .requestMatchers(
                                "/api/auth/**",           // /register, /login
                                "/swagger-ui/**",         // Interface Swagger
                                "/v3/api-docs/**",        // Documentation OpenAPI
                                "/swagger-ui.html"        // Page Swagger
                        ).permitAll()  // Autorise tout le monde
                        // Toutes les AUTRES routes nécessitent une authentification
                        .anyRequest().authenticated()  // JWT requis
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // Pas de session côté serveur (JWT)
                )
                .authenticationProvider(authenticationProvider())  // Provider pour vérifier user/password
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);  // Ajoute notre filtre JWT AVANT le filtre par défaut

        return http.build();
    }

    @Bean  // Provider pour authentifier avec username + password
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);  // Service pour charger l'utilisateur
        authProvider.setPasswordEncoder(passwordEncoder());  // Encoder pour vérifier le password
        return authProvider;
    }

    @Bean  // Manager d'authentification utilisé par AuthService
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean  // Encoder BCrypt pour crypter/vérifier les passwords
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // Algo BCrypt (hash à sens unique)
    }
}

// Configuration centrale de la sécurité
// Flow : Requête → JwtAuthenticationFilter (vérifie token) → Controller (si authentifié)
// Routes publiques : /api/auth/**, /swagger-ui/** (pas de token requis)
// Routes protégées : Toutes les autres (token JWT obligatoire)
// STATELESS : Pas de session serveur, toute l'info est dans le token JWT