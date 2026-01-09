package pse.nebula.worldview.infrastructure.adapter.inbound.web.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import pse.nebula.worldview.domain.exception.JourneyAlreadyExistsException;
import pse.nebula.worldview.domain.exception.JourneyNotFoundException;
import pse.nebula.worldview.domain.exception.RouteNotFoundException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for GlobalExceptionHandler.
 */
@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Nested
    @DisplayName("IOException Handling")
    class IOExceptionHandlingTests {

        @ParameterizedTest(name = "Should handle IOException with message: \"{0}\"")
        @NullSource
        @ValueSource(strings = {
                "Broken pipe",
                "Connection reset by peer",
                "Connection closed",
                "Stream closed unexpectedly",
                "Some other IO error"
        })
        void shouldHandleIOExceptionWithVariousMessages(String message) {
            IOException ex = new IOException(message);

            ResponseEntity<Void> response = exceptionHandler.handleIOException(ex);

            assertNull(response);
        }
    }

    @Nested
    @DisplayName("AsyncRequestNotUsableException Handling")
    class AsyncRequestNotUsableExceptionTests {

        @Test
        @DisplayName("Should handle async request not usable")
        void shouldHandleAsyncRequestNotUsable() {
            AsyncRequestNotUsableException ex = new AsyncRequestNotUsableException("Client disconnected");

            ResponseEntity<Void> response = exceptionHandler.handleAsyncRequestNotUsable(ex);

            assertNull(response);
        }
    }

    @Nested
    @DisplayName("RouteNotFoundException Handling")
    class RouteNotFoundExceptionTests {

        @Test
        @DisplayName("Should return 404 for route not found")
        void shouldReturn404ForRouteNotFound() {
            RouteNotFoundException ex = new RouteNotFoundException("route-123");

            ResponseEntity<Map<String, Object>> response = exceptionHandler.handleRouteNotFound(ex);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(404, response.getBody().get("status"));
            assertEquals("ROUTE_NOT_FOUND", response.getBody().get("error"));
            assertTrue(response.getBody().get("message").toString().contains("route-123"));
            assertNotNull(response.getBody().get("timestamp"));
        }
    }

    @Nested
    @DisplayName("JourneyNotFoundException Handling")
    class JourneyNotFoundExceptionTests {

        @Test
        @DisplayName("Should return 404 for journey not found")
        void shouldReturn404ForJourneyNotFound() {
            JourneyNotFoundException ex = new JourneyNotFoundException("journey-456");

            ResponseEntity<Map<String, Object>> response = exceptionHandler.handleJourneyNotFound(ex);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(404, response.getBody().get("status"));
            assertEquals("JOURNEY_NOT_FOUND", response.getBody().get("error"));
            assertTrue(response.getBody().get("message").toString().contains("journey-456"));
        }
    }

    @Nested
    @DisplayName("JourneyAlreadyExistsException Handling")
    class JourneyAlreadyExistsExceptionTests {

        @Test
        @DisplayName("Should return 409 for journey already exists")
        void shouldReturn409ForJourneyAlreadyExists() {
            JourneyAlreadyExistsException ex = new JourneyAlreadyExistsException("journey-789");

            ResponseEntity<Map<String, Object>> response = exceptionHandler.handleJourneyAlreadyExists(ex);

            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(409, response.getBody().get("status"));
            assertEquals("JOURNEY_ALREADY_EXISTS", response.getBody().get("error"));
            assertTrue(response.getBody().get("message").toString().contains("journey-789"));
        }
    }

    @Nested
    @DisplayName("IllegalArgumentException Handling")
    class IllegalArgumentExceptionTests {

        @Test
        @DisplayName("Should return 400 for invalid argument")
        void shouldReturn400ForInvalidArgument() {
            IllegalArgumentException ex = new IllegalArgumentException("Invalid parameter value");

            ResponseEntity<Map<String, Object>> response = exceptionHandler.handleIllegalArgument(ex);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(400, response.getBody().get("status"));
            assertEquals("INVALID_ARGUMENT", response.getBody().get("error"));
            assertEquals("Invalid parameter value", response.getBody().get("message"));
        }
    }

    @Nested
    @DisplayName("IllegalStateException Handling")
    class IllegalStateExceptionTests {

        @Test
        @DisplayName("Should return 400 for invalid state")
        void shouldReturn400ForInvalidState() {
            IllegalStateException ex = new IllegalStateException("Cannot perform action in current state");

            ResponseEntity<Map<String, Object>> response = exceptionHandler.handleIllegalState(ex);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(400, response.getBody().get("status"));
            assertEquals("INVALID_STATE", response.getBody().get("error"));
            assertEquals("Cannot perform action in current state", response.getBody().get("message"));
        }
    }

    @Nested
    @DisplayName("MethodArgumentNotValidException Handling")
    class ValidationExceptionTests {

        @Test
        @DisplayName("Should return 400 with field errors for validation exception")
        void shouldReturn400WithFieldErrorsForValidationException() {
            BindingResult bindingResult = mock(BindingResult.class);
            FieldError fieldError1 = new FieldError("request", "journeyId", "must not be blank");
            FieldError fieldError2 = new FieldError("request", "speed", "must be positive");

            when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

            // Create mock for MethodArgumentNotValidException
            MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
            when(ex.getBindingResult()).thenReturn(bindingResult);

            ResponseEntity<Map<String, Object>> response = exceptionHandler.handleValidationErrors(ex);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(400, response.getBody().get("status"));
            assertEquals("VALIDATION_ERROR", response.getBody().get("error"));
            assertEquals("Validation failed", response.getBody().get("message"));

            @SuppressWarnings("unchecked")
            Map<String, String> fieldErrors = (Map<String, String>) response.getBody().get("field_errors");
            assertNotNull(fieldErrors);
            assertEquals("must not be blank", fieldErrors.get("journeyId"));
            assertEquals("must be positive", fieldErrors.get("speed"));
        }
    }

    @Nested
    @DisplayName("Generic Exception Handling")
    class GenericExceptionTests {

        @Test
        @DisplayName("Should return 500 for unexpected exceptions")
        void shouldReturn500ForUnexpectedExceptions() {
            Exception ex = new NullPointerException("Unexpected null");

            ResponseEntity<Map<String, Object>> response = exceptionHandler.handleGenericException(ex);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(500, response.getBody().get("status"));
            assertEquals("INTERNAL_ERROR", response.getBody().get("error"));
            assertEquals("An unexpected error occurred. Please try again later.", response.getBody().get("message"));
        }
    }
}