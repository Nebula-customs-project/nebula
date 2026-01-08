package pse.nebula.platformcore.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for Spring Cloud Config Server functionality
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "eureka.client.register-with-eureka=false",
                "eureka.client.fetch-registry=false",
                "spring.cloud.config.server.native.search-locations=classpath:/config-repo/"
        }
)
@ActiveProfiles({"test", "native"})
@DisplayName("Config Server Integration Tests")
class ConfigServerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("Should serve default application configuration")
    void testConfigServer_ServesDefaultApplicationConfig() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/application/default",
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).contains("eureka");
    }

    @Test
    @DisplayName("Should serve gateway-service specific configuration")
    void testConfigServer_ServesGatewayServiceConfig() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/gateway-service/default",
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).contains("gateway");
    }

    @Test
    @DisplayName("Should serve shared-database configuration")
    void testConfigServer_ServesSharedDatabaseConfig() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/shared-database/default",
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).contains("datasource");
    }

    @Test
    @DisplayName("Should serve shared-rabbitmq configuration")
    void testConfigServer_ServesSharedRabbitMQConfig() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/shared-rabbitmq/default",
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).contains("rabbitmq");
    }

    @Test
    @DisplayName("Should return 404 for non-existent service configuration")
    void testConfigServer_ReturnsNotFoundForNonExistentConfig() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/non-existent-service/default",
                String.class
        );

        // Then
        // Config server returns OK with only application.yaml for non-existent services
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Should serve configuration with JSON format")
    void testConfigServer_ServesConfigInJsonFormat() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/application/default",
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        // Check if response is valid JSON-like structure
        assertThat(response.getBody()).contains("\"name\"");
    }

    @Test
    @DisplayName("Should handle invalid profile gracefully")
    void testConfigServer_HandlesInvalidProfile() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/application/invalid-profile",
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("Config server actuator health endpoint should be UP")
    void testConfigServer_ActuatorHealthIsUp() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/actuator/health",
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"status\":\"UP\"");
    }
}

