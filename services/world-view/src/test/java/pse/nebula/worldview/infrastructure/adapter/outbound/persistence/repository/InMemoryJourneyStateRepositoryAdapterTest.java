package pse.nebula.worldview.infrastructure.adapter.outbound.persistence.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pse.nebula.worldview.domain.model.Coordinate;
import pse.nebula.worldview.domain.model.DrivingRoute;
import pse.nebula.worldview.domain.model.JourneyState;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for InMemoryJourneyStateRepositoryAdapter.
 * Tests the in-memory journey state storage.
 */
@DisplayName("InMemoryJourneyStateRepositoryAdapter Unit Tests")
class InMemoryJourneyStateRepositoryAdapterTest {

    private InMemoryJourneyStateRepositoryAdapter repository;
    private DrivingRoute testRoute;

    @BeforeEach
    void setUp() {
        repository = new InMemoryJourneyStateRepositoryAdapter();

        List<Coordinate> waypoints = Arrays.asList(
            new Coordinate(48.8973, 9.1920),
            new Coordinate(48.8800, 9.1750),
            new Coordinate(48.8354, 9.1520)
        );

        testRoute = new DrivingRoute(
            "test-route",
            "Test Route",
            "A test route",
            waypoints,
            10000,
            600
        );
    }

    @Nested
    @DisplayName("save() Tests")
    class SaveTests {

        @Test
        @DisplayName("Should save journey state successfully")
        void shouldSaveJourneyState() {
            // Given
            JourneyState journeyState = new JourneyState("journey-1", testRoute, 10.0);

            // When
            repository.save(journeyState);

            // Then
            assertTrue(repository.exists("journey-1"));
            assertEquals(1, repository.size());
        }

        @Test
        @DisplayName("Should update existing journey state")
        void shouldUpdateExistingJourneyState() {
            // Given
            JourneyState journeyState = new JourneyState("journey-1", testRoute, 10.0);
            repository.save(journeyState);

            // When
            journeyState.start();
            repository.save(journeyState);

            // Then
            Optional<JourneyState> retrieved = repository.findById("journey-1");
            assertTrue(retrieved.isPresent());
            assertEquals("IN_PROGRESS", retrieved.get().getStatus().name());
            assertEquals(1, repository.size());
        }

        @Test
        @DisplayName("Should save multiple journey states")
        void shouldSaveMultipleJourneyStates() {
            // Given
            JourneyState journey1 = new JourneyState("journey-1", testRoute, 10.0);
            JourneyState journey2 = new JourneyState("journey-2", testRoute, 15.0);

            // When
            repository.save(journey1);
            repository.save(journey2);

            // Then
            assertEquals(2, repository.size());
            assertTrue(repository.exists("journey-1"));
            assertTrue(repository.exists("journey-2"));
        }
    }

    @Nested
    @DisplayName("findById() Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should find existing journey state")
        void shouldFindExistingJourneyState() {
            // Given
            JourneyState journeyState = new JourneyState("journey-1", testRoute, 10.0);
            repository.save(journeyState);

            // When
            Optional<JourneyState> result = repository.findById("journey-1");

            // Then
            assertTrue(result.isPresent());
            assertEquals("journey-1", result.get().getJourneyId());
            assertEquals(10.0, result.get().getSpeedMetersPerSecond());
        }

        @Test
        @DisplayName("Should return empty for non-existent journey")
        void shouldReturnEmptyForNonExistentJourney() {
            // When
            Optional<JourneyState> result = repository.findById("non-existent");

            // Then
            assertFalse(result.isPresent());
        }
    }

    @Nested
    @DisplayName("delete() Tests")
    class DeleteTests {

        @Test
        @DisplayName("Should delete existing journey state")
        void shouldDeleteExistingJourneyState() {
            // Given
            JourneyState journeyState = new JourneyState("journey-1", testRoute, 10.0);
            repository.save(journeyState);
            assertTrue(repository.exists("journey-1"));

            // When
            repository.delete("journey-1");

            // Then
            assertFalse(repository.exists("journey-1"));
            assertEquals(0, repository.size());
        }

        @Test
        @DisplayName("Should handle delete of non-existent journey gracefully")
        void shouldHandleDeleteOfNonExistentJourney() {
            // When & Then - should not throw
            assertDoesNotThrow(() -> repository.delete("non-existent"));
        }
    }

    @Nested
    @DisplayName("exists() Tests")
    class ExistsTests {

        @Test
        @DisplayName("Should return true for existing journey")
        void shouldReturnTrueForExistingJourney() {
            // Given
            JourneyState journeyState = new JourneyState("journey-1", testRoute, 10.0);
            repository.save(journeyState);

            // When & Then
            assertTrue(repository.exists("journey-1"));
        }

        @Test
        @DisplayName("Should return false for non-existent journey")
        void shouldReturnFalseForNonExistentJourney() {
            // When & Then
            assertFalse(repository.exists("non-existent"));
        }
    }

    @Nested
    @DisplayName("clear() Tests")
    class ClearTests {

        @Test
        @DisplayName("Should clear all journey states")
        void shouldClearAllJourneyStates() {
            // Given
            repository.save(new JourneyState("journey-1", testRoute, 10.0));
            repository.save(new JourneyState("journey-2", testRoute, 15.0));
            assertEquals(2, repository.size());

            // When
            repository.clear();

            // Then
            assertEquals(0, repository.size());
            assertFalse(repository.exists("journey-1"));
            assertFalse(repository.exists("journey-2"));
        }

        @Test
        @DisplayName("Should handle clear on empty repository")
        void shouldHandleClearOnEmptyRepository() {
            // When & Then - should not throw
            assertDoesNotThrow(() -> repository.clear());
            assertEquals(0, repository.size());
        }
    }

    @Nested
    @DisplayName("size() Tests")
    class SizeTests {

        @Test
        @DisplayName("Should return 0 for empty repository")
        void shouldReturnZeroForEmptyRepository() {
            assertEquals(0, repository.size());
        }

        @Test
        @DisplayName("Should return correct count after operations")
        void shouldReturnCorrectCountAfterOperations() {
            // Save 3 journeys
            repository.save(new JourneyState("journey-1", testRoute, 10.0));
            repository.save(new JourneyState("journey-2", testRoute, 15.0));
            repository.save(new JourneyState("journey-3", testRoute, 20.0));
            assertEquals(3, repository.size());

            // Delete one
            repository.delete("journey-2");
            assertEquals(2, repository.size());

            // Save another
            repository.save(new JourneyState("journey-4", testRoute, 25.0));
            assertEquals(3, repository.size());
        }
    }

    @Nested
    @DisplayName("Thread Safety Tests")
    class ThreadSafetyTests {

        @Test
        @DisplayName("Should handle concurrent saves without error")
        void shouldHandleConcurrentSaves() throws InterruptedException {
            // Given
            int threadCount = 10;
            Thread[] threads = new Thread[threadCount];

            // When
            for (int i = 0; i < threadCount; i++) {
                final int index = i;
                threads[i] = new Thread(() -> {
                    JourneyState state = new JourneyState("journey-" + index, testRoute, 10.0 + index);
                    repository.save(state);
                });
            }

            for (Thread thread : threads) {
                thread.start();
            }

            for (Thread thread : threads) {
                thread.join();
            }

            // Then
            assertEquals(threadCount, repository.size());
        }
    }
}