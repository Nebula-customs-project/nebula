package pse.nebula.worldview.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for domain exceptions.
 */
@DisplayName("Domain Exception Tests")
class DomainExceptionTest {

    @Nested
    @DisplayName("JourneyNotFoundException Tests")
    class JourneyNotFoundExceptionTests {

        @Test
        @DisplayName("Should create exception with journey ID")
        void shouldCreateExceptionWithJourneyId() {
            JourneyNotFoundException exception = new JourneyNotFoundException("journey-123");

            assertEquals("Journey not found with ID: journey-123", exception.getMessage());
            assertNull(exception.getCause());
        }

        @Test
        @DisplayName("Should create exception with message and cause")
        void shouldCreateExceptionWithMessageAndCause() {
            Throwable cause = new RuntimeException("Database error");
            JourneyNotFoundException exception = new JourneyNotFoundException("Custom message", cause);

            assertEquals("Custom message", exception.getMessage());
            assertEquals(cause, exception.getCause());
        }

        @Test
        @DisplayName("Should extend DomainException and RuntimeException")
        void shouldExtendDomainExceptionAndRuntimeException() {
            // Verify class hierarchy at compile time
            assertEquals(DomainException.class, JourneyNotFoundException.class.getSuperclass());
            assertTrue(RuntimeException.class.isAssignableFrom(JourneyNotFoundException.class));
        }
    }

    @Nested
    @DisplayName("JourneyAlreadyExistsException Tests")
    class JourneyAlreadyExistsExceptionTests {

        @Test
        @DisplayName("Should create exception with journey ID")
        void shouldCreateExceptionWithJourneyId() {
            JourneyAlreadyExistsException exception = new JourneyAlreadyExistsException("journey-456");

            assertEquals("Journey already exists with ID: journey-456", exception.getMessage());
            assertNull(exception.getCause());
        }

        @Test
        @DisplayName("Should create exception with message and cause")
        void shouldCreateExceptionWithMessageAndCause() {
            Throwable cause = new RuntimeException("Duplicate key");
            JourneyAlreadyExistsException exception = new JourneyAlreadyExistsException("Custom message", cause);

            assertEquals("Custom message", exception.getMessage());
            assertEquals(cause, exception.getCause());
        }

        @Test
        @DisplayName("Should extend DomainException and RuntimeException")
        void shouldExtendDomainExceptionAndRuntimeException() {
            // Verify class hierarchy at compile time
            assertEquals(DomainException.class, JourneyAlreadyExistsException.class.getSuperclass());
            assertTrue(RuntimeException.class.isAssignableFrom(JourneyAlreadyExistsException.class));
        }
    }

    @Nested
    @DisplayName("RouteNotFoundException Tests")
    class RouteNotFoundExceptionTests {

        @Test
        @DisplayName("Should create exception with route ID")
        void shouldCreateExceptionWithRouteId() {
            RouteNotFoundException exception = new RouteNotFoundException("route-789");

            assertEquals("Route not found with ID: route-789", exception.getMessage());
            assertNull(exception.getCause());
        }

        @Test
        @DisplayName("Should create exception with message and cause")
        void shouldCreateExceptionWithMessageAndCause() {
            Throwable cause = new RuntimeException("Database error");
            RouteNotFoundException exception = new RouteNotFoundException("Custom route error", cause);

            assertEquals("Custom route error", exception.getMessage());
            assertEquals(cause, exception.getCause());
        }

        @Test
        @DisplayName("Should create no routes available exception")
        void shouldCreateNoRoutesAvailableException() {
            RouteNotFoundException exception = RouteNotFoundException.noRoutesAvailable();

            assertEquals("No routes available", exception.getMessage());
            assertNull(exception.getCause());
        }

        @Test
        @DisplayName("Should extend DomainException and RuntimeException")
        void shouldExtendDomainExceptionAndRuntimeException() {
            // Verify class hierarchy at compile time
            assertEquals(DomainException.class, RouteNotFoundException.class.getSuperclass());
            assertTrue(RuntimeException.class.isAssignableFrom(RouteNotFoundException.class));
        }
    }
}