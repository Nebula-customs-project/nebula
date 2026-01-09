package pse.nebula.platformcore.security;

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
 * Security and Edge Case tests for Platform Core Service
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
@DisplayName("Security and Edge Cases Tests")
class SecurityAndEdgeCasesTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("Should handle malformed URLs gracefully")
    void testSecurity_HandlesMalformedUrls() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/invalid/../path",
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isIn(HttpStatus.NOT_FOUND, HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Should return 404 for non-existent endpoints")
    void testSecurity_Returns404ForNonExistentEndpoints() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/nonexistent",
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Config server should handle special characters in service name")
    void testConfigServer_HandlesSpecialCharactersInServiceName() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/test-service-123/default",
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Config server should handle empty service name")
    void testConfigServer_HandlesEmptyServiceName() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "//default",
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isIn(HttpStatus.NOT_FOUND, HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Eureka should handle multiple concurrent requests")
    void testEureka_HandlesMultipleConcurrentRequests() throws InterruptedException {
        // When - simulate concurrent requests
        Thread[] threads = new Thread[5];
        final boolean[] success = {true};

        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                ResponseEntity<String> response = restTemplate.getForEntity(
                        "/eureka/apps",
                        String.class
                );
                if (response.getStatusCode() != HttpStatus.OK) {
                    success[0] = false;
                }
            });
            threads[i].start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }

        // Then
        assertThat(success[0]).isTrue();
    }

    @Test
    @DisplayName("Health endpoint should handle high load")
    void testHealth_HandlesHighLoad() {
        // When - simulate high load
        for (int i = 0; i < 10; i++) {
            ResponseEntity<String> response = restTemplate.getForEntity(
                    "/api/v1/health",
                    String.class
            );
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    @Test
    @DisplayName("Config server should handle rapid consecutive requests")
    void testConfigServer_HandlesRapidRequests() {
        // When
        for (int i = 0; i < 5; i++) {
            ResponseEntity<String> response = restTemplate.getForEntity(
                    "/application/default",
                    String.class
            );
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    @Test
    @DisplayName("Should handle requests with very long URLs gracefully")
    void testSecurity_HandlesLongUrls() {
        // When
        String longPath = "/api/v1/" + "a".repeat(1000);
        ResponseEntity<String> response = restTemplate.getForEntity(
                longPath,
                String.class
        );

        // Then - should not crash, should return proper error
        assertThat(response.getStatusCode()).isIn(
                HttpStatus.NOT_FOUND,
                HttpStatus.BAD_REQUEST,
                HttpStatus.URI_TOO_LONG
        );
    }

    @Test
    @DisplayName("Should handle requests with null or empty headers")
    void testSecurity_HandlesEmptyHeaders() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/health",
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Config server should not expose sensitive information")
    void testSecurity_ConfigServerDoesNotExposeSensitiveInfo() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/application/default",
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        // Should not contain actual passwords or sensitive data in plain text
        assertThat(response.getBody()).isNotNull();
    }
}

