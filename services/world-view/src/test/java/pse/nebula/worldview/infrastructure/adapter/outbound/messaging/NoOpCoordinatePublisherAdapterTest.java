package pse.nebula.worldview.infrastructure.adapter.outbound.messaging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pse.nebula.worldview.domain.model.Coordinate;
import pse.nebula.worldview.domain.model.DrivingRoute;
import pse.nebula.worldview.domain.model.JourneyState;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for NoOpCoordinatePublisherAdapter.
 */
@DisplayName("NoOpCoordinatePublisherAdapter Tests")
class NoOpCoordinatePublisherAdapterTest {

    private NoOpCoordinatePublisherAdapter adapter;
    private JourneyState testJourneyState;

    @BeforeEach
    void setUp() {
        adapter = new NoOpCoordinatePublisherAdapter();

        List<Coordinate> waypoints = Arrays.asList(
                new Coordinate(48.8973, 9.1920),
                new Coordinate(48.8354, 9.1520)
        );

        DrivingRoute testRoute = new DrivingRoute(
                "route-1",
                "Test Route",
                "Test description",
                waypoints,
                5000.0,
                600
        );

        testJourneyState = new JourneyState("journey-1", testRoute, 13.89);
        testJourneyState.start();
    }

    @Test
    @DisplayName("Should not throw when publishing coordinate update")
    void shouldNotThrowWhenPublishingCoordinateUpdate() {
        Coordinate coordinate = new Coordinate(48.86, 9.17);

        assertDoesNotThrow(() ->
                adapter.publishCoordinateUpdate("journey-1", coordinate, testJourneyState));
    }

    @Test
    @DisplayName("Should not throw when publishing journey started")
    void shouldNotThrowWhenPublishingJourneyStarted() {
        assertDoesNotThrow(() ->
                adapter.publishJourneyStarted(testJourneyState));
    }

    @Test
    @DisplayName("Should not throw when publishing journey completed")
    void shouldNotThrowWhenPublishingJourneyCompleted() {
        assertDoesNotThrow(() ->
                adapter.publishJourneyCompleted(testJourneyState));
    }

    @Test
    @DisplayName("Should handle multiple calls without issues")
    void shouldHandleMultipleCallsWithoutIssues() {
        Coordinate coordinate = new Coordinate(48.86, 9.17);

        for (int i = 0; i < 100; i++) {
            final int index = i;
            assertDoesNotThrow(() -> {
                adapter.publishCoordinateUpdate("journey-" + index, coordinate, testJourneyState);
                adapter.publishJourneyStarted(testJourneyState);
                adapter.publishJourneyCompleted(testJourneyState);
            });
        }
    }
}