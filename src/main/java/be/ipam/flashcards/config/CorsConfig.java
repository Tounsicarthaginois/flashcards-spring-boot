package be.ipam.flashcards.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Autorise les requêtes venant d'Angular
        config.addAllowedOrigin("http://localhost:4200");

        // Autorise tous les headers
        config.addAllowedHeader("*");

        // Autorise toutes les méthodes HTTP (GET, POST, PUT, DELETE...)
        config.addAllowedMethod("*");

        // Autorise l'envoi du token JWT dans les headers
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
