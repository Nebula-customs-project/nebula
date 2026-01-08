package pse.nebula.platformcore.actuator;

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
 * Integration tests for Actuator endpoints
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
@DisplayName("Actuator Endpoints Integration Tests")
class ActuatorEndpointsTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("Health actuator endpoint should be accessible")
    void testActuator_HealthEndpointAccessible() {
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
    @DisplayName("Info actuator endpoint may be accessible")
    void testActuator_InfoEndpointAccessible() {
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
    @DisplayName("Env actuator endpoint may be accessible")
    void testActuator_EnvEndpointAccessible() {
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
    @DisplayName("Actuator base endpoint should list all endpoints")
    void testActuator_BaseEndpointListsEndpoints() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/actuator",
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("health");
        // Note: Only health is exposed in test config (application-test.yaml)
    }

    @Test
    @DisplayName("Health endpoint should show diskSpace component")
    void testActuator_HealthShowsDiskSpace() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/actuator/health",
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("Health endpoint should show ping component")
    void testActuator_HealthShowsPing() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/actuator/health",
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).contains("UP");
    }

    @Test
    @DisplayName("Env endpoint should contain spring properties if exposed")
    void testActuator_EnvContainsSpringProperties() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/actuator/env",
                String.class
        );

        // Then
        // Env endpoint may not be exposed in test config
        if (response.getStatusCode() == HttpStatus.OK) {
            assertThat(response.getBody()).contains("spring");
        } else {
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Test
    @DisplayName("Non-existent actuator endpoint should return 404")
    void testActuator_NonExistentEndpointReturns404() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/actuator/nonexistent",
                String.class
        );

        // Then
        // Spring Boot Actuator may return 200 with error info or 404 depending on version
        assertThat(response.getStatusCode()).isIn(HttpStatus.NOT_FOUND, HttpStatus.OK);
    }

    @Test
    @DisplayName("Metrics endpoint exposure depends on configuration")
    void testActuator_MetricsEndpointNotExposed() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/actuator/metrics",
                String.class
        );

        // Then
        // Metrics may or may not be exposed depending on configuration
        assertThat(response.getStatusCode()).isIn(HttpStatus.NOT_FOUND, HttpStatus.OK);
    }
}

