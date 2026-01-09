package pse.nebula.gateway.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Unit tests for HealthController
 */
@WebFluxTest(controllers = HealthController.class)
@ContextConfiguration(classes = HealthController.class)
class HealthControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testHealth_ReturnsStatusUp() {
        // When: Calling health endpoint
        webTestClient.get()
                .uri("/api/v1/health")
                .exchange()
                // Then: Should return 200 OK with status UP
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("UP")
                .jsonPath("$.service").isEqualTo("gateway-service");
    }

    @Test
    void testHealth_ReturnsJsonContentType() {
        // When: Calling health endpoint
        webTestClient.get()
                .uri("/api/v1/health")
                .exchange()
                // Then: Should return JSON content type
                .expectStatus().isOk()
                .expectHeader().contentType("application/json");
    }

    @Test
    void testHealth_MultipleCalls_ReturnsConsistentResponse() {
        // When: Calling health endpoint multiple times
        for (int i = 0; i < 5; i++) {
            webTestClient.get()
                    .uri("/api/v1/health")
                    .exchange()
                    // Then: Should always return same response
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.status").isEqualTo("UP")
                    .jsonPath("$.service").isEqualTo("gateway-service");
        }
    }
}

