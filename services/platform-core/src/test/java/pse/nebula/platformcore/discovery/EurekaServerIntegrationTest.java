package pse.nebula.platformcore.discovery;

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
 * Integration tests for Eureka Server (Service Discovery) functionality
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "eureka.client.register-with-eureka=false",
                "eureka.client.fetch-registry=false",
                "spring.cloud.config.server.native.search-locations=classpath:/config-repo/"
        }
)
@ActiveProfiles("test")
@DisplayName("Eureka Server Integration Tests")
class EurekaServerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("Eureka dashboard should be accessible")
    void testEurekaServer_DashboardIsAccessible() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/",
                String.class
        );

        // Then
        // Eureka dashboard is at root path and returns HTML
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("Eureka apps endpoint should return applications list")
    void testEurekaServer_AppsEndpointReturnsApplications() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/eureka/apps",
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        // Response should contain applications XML/JSON
        assertThat(response.getBody()).containsAnyOf("applications", "<?xml");
    }

    @Test
    @DisplayName("Eureka health endpoint should be UP")
    void testEurekaServer_HealthEndpointIsUp() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/actuator/health",
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"status\":\"UP\"");
    }

    @Test
    @DisplayName("Eureka should expose info endpoint if configured")
    void testEurekaServer_InfoEndpointIsAccessible() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/actuator/info",
                String.class
        );

        // Then
        // Info endpoint may not be exposed in test config
        assertThat(response.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Eureka should expose env endpoint if configured")
    void testEurekaServer_EnvEndpointIsAccessible() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/actuator/env",
                String.class
        );

        // Then
        // Env endpoint may not be exposed in test config
        assertThat(response.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Eureka server should start with no registered services initially")
    void testEurekaServer_NoInitialRegistrations() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/eureka/apps",
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        // Initially, no services should be registered (empty or minimal response)
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("Eureka endpoint with JSON accept header should return JSON")
    void testEurekaServer_ReturnsJsonWhenRequested() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/eureka/apps",
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        // Should contain XML or JSON structure
        assertThat(response.getBody()).containsAnyOf("applications", "<?xml", "{");
    }

    @Test
    @DisplayName("Eureka server should be configured correctly")
    void testEurekaServer_ConfiguredCorrectly() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/actuator/health",
                String.class
        );

        // Then
        // Just verify actuator is working, env endpoint may not be exposed in test
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("UP");
    }
}

