package pse.nebula.platformcore.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for HealthController
 */
@DisplayName("HealthController Unit Tests")
class HealthControllerTest {

    private HealthController healthController;

    @BeforeEach
    void setUp() {
        healthController = new HealthController();
    }

    @Test
    @DisplayName("Should return UP status with correct service name")
    void testHealthEndpoint_ReturnsUpStatus() {
        // When
        ResponseEntity<Map<String, String>> response = healthController.health();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsEntry("status", "UP");
        assertThat(response.getBody()).containsEntry("service", "platform-core");
    }

    @Test
    @DisplayName("Should return response with exactly two keys")
    void testHealthEndpoint_ReturnsCorrectNumberOfKeys() {
        // When
        ResponseEntity<Map<String, String>> response = healthController.health();

        // Then
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()).containsOnlyKeys("status", "service");
    }

    @Test
    @DisplayName("Should return 200 OK status code")
    void testHealthEndpoint_ReturnsOkStatusCode() {
        // When
        ResponseEntity<Map<String, String>> response = healthController.health();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Should return non-null response body")
    void testHealthEndpoint_ReturnsNonNullBody() {
        // When
        ResponseEntity<Map<String, String>> response = healthController.health();

        // Then
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("Should return consistent response on multiple calls")
    void testHealthEndpoint_ConsistentAcrossMultipleCalls() {
        // When
        ResponseEntity<Map<String, String>> response1 = healthController.health();
        ResponseEntity<Map<String, String>> response2 = healthController.health();
        ResponseEntity<Map<String, String>> response3 = healthController.health();

        // Then
        assertThat(response1.getBody()).isEqualTo(response2.getBody());
        assertThat(response2.getBody()).isEqualTo(response3.getBody());
    }
}

