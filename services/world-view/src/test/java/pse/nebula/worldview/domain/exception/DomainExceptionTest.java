package pse.nebula.worldview.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Domain Exception Unit Tests")
class DomainExceptionTest {

    @Nested
    @DisplayName("JourneyNotFoundException Tests")
    class JourneyNotFoundExceptionTests {

        @Test
        @DisplayName("Should create exception with journey ID message")
        void shouldCreateExceptionWithJourneyIdMessage() {
            JourneyNotFoundException exception = new JourneyNotFoundException("journey-123");

            assertEquals("Journey not found with ID: journey-123", exception.getMessage());
            assertInstanceOf(DomainException.class, exception);
            assertInstanceOf(RuntimeException.class, exception);
        }

        @Test
        @DisplayName("Should create exception with message and cause")
        void shouldCreateExceptionWithMessageAndCause() {
            Throwable cause = new RuntimeException("Original cause");
            JourneyNotFoundException exception = new JourneyNotFoundException("Custom message", cause);

            assertEquals("Custom message", exception.getMessage());
            assertEquals(cause, exception.getCause());
        }
    }

    @Nested
    @DisplayName("RouteNotFoundException Tests")
    class RouteNotFoundExceptionTests {

        @Test
        @DisplayName("Should create exception with route ID message")
        void shouldCreateExceptionWithRouteIdMessage() {
            RouteNotFoundException exception = new RouteNotFoundException("route-1");

            assertEquals("Route not found with ID: route-1", exception.getMessage());
            assertInstanceOf(DomainException.class, exception);
        }

        @Test
        @DisplayName("Should create exception with message and cause")
        void shouldCreateExceptionWithMessageAndCause() {
            Throwable cause = new RuntimeException("Original cause");
            RouteNotFoundException exception = new RouteNotFoundException("Custom message", cause);

            assertEquals("Custom message", exception.getMessage());
            assertEquals(cause, exception.getCause());
        }

        @Test
        @DisplayName("noRoutesAvailable factory should create proper exception")
        void noRoutesAvailableFactoryShouldCreateProperException() {
            RouteNotFoundException exception = RouteNotFoundException.noRoutesAvailable();

            assertEquals("No routes available", exception.getMessage());
            assertNull(exception.getCause());
        }
    }

    @Nested
    @DisplayName("JourneyAlreadyExistsException Tests")
    class JourneyAlreadyExistsExceptionTests {

        @Test
        @DisplayName("Should create exception with journey ID message")
        void shouldCreateExceptionWithJourneyIdMessage() {
            JourneyAlreadyExistsException exception = new JourneyAlreadyExistsException("journey-456");

            assertEquals("Journey already exists with ID: journey-456", exception.getMessage());
            assertInstanceOf(DomainException.class, exception);
        }

        @Test
        @DisplayName("Should create exception with message and cause")
        void shouldCreateExceptionWithMessageAndCause() {
            Throwable cause = new RuntimeException("Original cause");
            JourneyAlreadyExistsException exception = new JourneyAlreadyExistsException("Custom message", cause);

            assertEquals("Custom message", exception.getMessage());
            assertEquals(cause, exception.getCause());
        }
    }
}

