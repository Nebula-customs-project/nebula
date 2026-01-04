package pse.nebula.worldview.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration for the World-View Service.
 * Provides interactive API documentation at /swagger-ui.html
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8082}")
    private String serverPort;

    @Bean
    public OpenAPI worldViewOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("World-View Service API")
                        .description("""
                                REST API for the World-View journey tracking service.
                                
                                ## Features
                                - **Routes**: Get predefined driving routes to the dealership
                                - **Journeys**: Start, pause, resume, and stop journey simulations
                                - **Real-time Updates**: Subscribe to MQTT for live position updates
                                
                                ## Real-time Streaming
                                Position updates are streamed via MQTT (not REST).
                                - **Broker**: ws://localhost:15675/ws
                                - **Topic**: nebula/journey/{journeyId}/position
                                
                                See the MQTT Streaming Guide for details.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Nebula Team")
                                .email("team@nebula.pse"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local Development Server")))
                .tags(List.of(
                        new Tag()
                                .name("Routes")
                                .description("Endpoints for managing driving routes"),
                        new Tag()
                                .name("Journeys")
                                .description("Endpoints for journey lifecycle management")));
    }
}

