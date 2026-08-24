
package be.ipam.flashcards.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration  // Spring va scanner cette classe au démarrage
public class OpenApiConfig {

    @Bean  // Crée un objet OpenAPI géré par Spring
    public OpenAPI flashcardsApi() {

        final String securitySchemeName = "bearerAuth";  // Nom pour le schéma JWT

        return new OpenAPI()
                .info(new Info()  // Infos affichées en haut de Swagger
                        .title("Flashcards Management API")
                        .description("API REST pour gérer les flashcards et les utilisateurs avec authentification JWT")
                        .version("1.0.0"))

                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))  // Toutes les routes nécessitent l'auth

                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,  // Définit comment marche l'auth JWT
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)  // Type HTTP (pas OAuth)
                                        .scheme("bearer")  // Authorization: Bearer <token>
                                        .bearerFormat("JWT")));  // Format du token
    }

    // Résultat : bouton "Authorize" dans Swagger pour coller le token JWT
}