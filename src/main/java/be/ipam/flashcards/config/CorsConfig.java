package be.ipam.flashcards.config;

// Imports nécessaires pour la configuration CORS
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Indique à Spring que cette classe contient de la configuration
@Configuration
public class CorsConfig {

    // Crée un composant Spring qui sera chargé automatiquement au démarrage
    @Bean
    public WebMvcConfigurer corsConfigurer() {

        // On retourne une nouvelle configuration CORS
        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(CorsRegistry registry) {

                registry.addMapping("/**")         // Applique la règle sur TOUS les endpoints du backend
                        .allowedOrigins("*")       // Accepte les requêtes venant de n'importe quelle adresse (ex: localhost:4200)
                        .allowedMethods("*")       // Autorise toutes les méthodes HTTP : GET, POST, PUT, DELETE...
                        .allowedHeaders("*")       // Autorise tous les headers (ex: Authorization, Content-Type...)
                        .allowCredentials(false);  // Doit être false quand allowedOrigins est "*"
            }
        };
    }
}
