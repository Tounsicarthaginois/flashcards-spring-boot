package be.ipam.flashcards.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration OpenAPI pour Swagger UI
 *
 * Cette classe configure la documentation automatique de l'API REST
 * Accessible à l'URL : http://localhost:8080/swagger-ui.html
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI flashcardsApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Flashcards Management API")
                        .description("API REST pour gérer les flashcards et les utilisateurs")
                        .version("1.0.0"));
    }
}