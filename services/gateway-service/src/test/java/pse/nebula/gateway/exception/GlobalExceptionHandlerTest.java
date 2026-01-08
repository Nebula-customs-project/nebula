package pse.nebula.gateway.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GlobalExceptionHandler
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testHandleGenericException_ReturnsInternalServerError() {
        // Given: A generic exception
        Exception exception = new Exception("Test error message");

        // When: Handling the exception
        Mono<ResponseEntity<Map<String, Object>>> result = handler.handleGenericException(exception);

        // Then: Should return 500 Internal Server Error
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
                    assertNotNull(response.getBody());

                    Map<String, Object> body = response.getBody();
                    assertNotNull(body.get("timestamp"));
                    assertEquals(500, body.get("status"));
                    assertEquals("Internal Server Error", body.get("error"));
                    assertEquals("Test error message", body.get("message"));
                })
                .verifyComplete();
    }

    @Test
    void testHandleGenericException_NullMessage_ReturnsDefaultMessage() {
        // Given: An exception with null message
        Exception exception = new Exception((String) null);

        // When: Handling the exception
        Mono<ResponseEntity<Map<String, Object>>> result = handler.handleGenericException(exception);

        // Then: Should return default error message
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertNotNull(response.getBody());
                    Map<String, Object> body = response.getBody();
                    assertEquals("An unexpected error occurred", body.get("message"));
                })
                .verifyComplete();
    }

    @Test
    void testHandleGenericException_RuntimeException_ReturnsCorrectStatus() {
        // Given: A RuntimeException
        RuntimeException exception = new RuntimeException("Runtime error");

        // When: Handling the exception
        Mono<ResponseEntity<Map<String, Object>>> result = handler.handleGenericException(exception);

        // Then: Should return 500 with error message
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
                    Map<String, Object> body = response.getBody();
                    assertEquals("Runtime error", body.get("message"));
                })
                .verifyComplete();
    }

    @Test
    void testHandleGenericException_IllegalArgumentException_ReturnsCorrectStatus() {
        // Given: An IllegalArgumentException
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");

        // When: Handling the exception
        Mono<ResponseEntity<Map<String, Object>>> result = handler.handleGenericException(exception);

        // Then: Should return 500 with error message
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
                    Map<String, Object> body = response.getBody();
                    assertEquals("Invalid argument", body.get("message"));
                })
                .verifyComplete();
    }

    @Test
    void testHandleGenericException_NullPointerException_ReturnsCorrectStatus() {
        // Given: A NullPointerException
        NullPointerException exception = new NullPointerException("Null value encountered");

        // When: Handling the exception
        Mono<ResponseEntity<Map<String, Object>>> result = handler.handleGenericException(exception);

        // Then: Should return 500 with error message
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
                    Map<String, Object> body = response.getBody();
                    assertEquals("Null value encountered", body.get("message"));
                })
                .verifyComplete();
    }

    @Test
    void testHandleGenericException_ResponseBodyStructure_IsCorrect() {
        // Given: An exception
        Exception exception = new Exception("Test error");

        // When: Handling the exception
        Mono<ResponseEntity<Map<String, Object>>> result = handler.handleGenericException(exception);

        // Then: Response body should have required fields
        StepVerifier.create(result)
                .assertNext(response -> {
                    Map<String, Object> body = response.getBody();
                    assertNotNull(body);

                    // Check all required fields exist
                    assertTrue(body.containsKey("timestamp"));
                    assertTrue(body.containsKey("status"));
                    assertTrue(body.containsKey("error"));
                    assertTrue(body.containsKey("message"));

                    // Check field types
                    assertTrue(body.get("timestamp") instanceof String);
                    assertTrue(body.get("status") instanceof Integer);
                    assertTrue(body.get("error") instanceof String);
                    assertTrue(body.get("message") instanceof String);
                })
                .verifyComplete();
    }

    @Test
    void testHandleGenericException_TimestampFormat_IsISO8601() {
        // Given: An exception
        Exception exception = new Exception("Test error");

        // When: Handling the exception
        Mono<ResponseEntity<Map<String, Object>>> result = handler.handleGenericException(exception);

        // Then: Timestamp should be in ISO 8601 format
        StepVerifier.create(result)
                .assertNext(response -> {
                    Map<String, Object> body = response.getBody();
                    String timestamp = (String) body.get("timestamp");

                    // Basic ISO 8601 format check (contains T and Z)
                    assertNotNull(timestamp);
                    assertTrue(timestamp.contains("T"));
                    assertTrue(timestamp.endsWith("Z"));
                })
                .verifyComplete();
    }

    @Test
    void testHandleGenericException_SpecialCharactersInMessage_HandledCorrectly() {
        // Given: An exception with special characters
        Exception exception = new Exception("Error with special chars: @#$%^&*()");

        // When: Handling the exception
        Mono<ResponseEntity<Map<String, Object>>> result = handler.handleGenericException(exception);

        // Then: Should handle special characters correctly
        StepVerifier.create(result)
                .assertNext(response -> {
                    Map<String, Object> body = response.getBody();
                    assertEquals("Error with special chars: @#$%^&*()", body.get("message"));
                })
                .verifyComplete();
    }

    @Test
    void testHandleGenericException_LongErrorMessage_HandledCorrectly() {
        // Given: An exception with a long message
        String longMessage = "This is a very long error message that contains a lot of details about what went wrong in the system. ".repeat(5);
        Exception exception = new Exception(longMessage);

        // When: Handling the exception
        Mono<ResponseEntity<Map<String, Object>>> result = handler.handleGenericException(exception);

        // Then: Should handle long message correctly
        StepVerifier.create(result)
                .assertNext(response -> {
                    Map<String, Object> body = response.getBody();
                    assertEquals(longMessage, body.get("message"));
                })
                .verifyComplete();
    }

    @Test
    void testHandleGenericException_MultipleCalls_ReturnsConsistentStructure() {
        // Given: Multiple exceptions
        Exception[] exceptions = {
                new Exception("Error 1"),
                new Exception("Error 2"),
                new Exception("Error 3")
        };

        // When: Handling multiple exceptions
        for (Exception exception : exceptions) {
            Mono<ResponseEntity<Map<String, Object>>> result = handler.handleGenericException(exception);

            // Then: All should have consistent structure
            StepVerifier.create(result)
                    .assertNext(response -> {
                        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
                        Map<String, Object> body = response.getBody();
                        assertNotNull(body);
                        assertEquals(4, body.size()); // timestamp, status, error, message
                    })
                    .verifyComplete();
        }
    }
}

