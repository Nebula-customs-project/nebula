package pse.nebula.worldview.domain.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JourneyStateTest {

    private DrivingRoute createTestRoute() {
        List<Coordinate> waypoints = Arrays.asList(
            new Coordinate(48.9, 9.2),
            new Coordinate(48.88, 9.18),
            new Coordinate(48.86, 9.16),
            new Coordinate(48.8354, 9.152) // Dealership
        );

        return new DrivingRoute(
            "test-route",
            "Test Route",
            "A test route",
            waypoints.get(0),
            waypoints.get(waypoints.size() - 1),
            waypoints,
            10000,
            600
        );
    }

    @Test
    void shouldCreateJourneyWithNotStartedStatus() {
        DrivingRoute route = createTestRoute();
        JourneyState journey = new JourneyState("journey-1", route, 10.0);

        assertEquals(JourneyStatus.NOT_STARTED, journey.getStatus());
        assertEquals(0, journey.getCurrentWaypointIndex());
        assertEquals(0.0, journey.getProgressPercentage());
    }

    @Test
    void shouldStartJourney() {
        DrivingRoute route = createTestRoute();
        JourneyState journey = new JourneyState("journey-1", route, 10.0);

        journey.start();

        assertEquals(JourneyStatus.IN_PROGRESS, journey.getStatus());
    }

    @Test
    void shouldPauseAndResumeJourney() {
        DrivingRoute route = createTestRoute();
        JourneyState journey = new JourneyState("journey-1", route, 10.0);

        journey.start();
        journey.pause();
        assertEquals(JourneyStatus.PAUSED, journey.getStatus());

        journey.resume();
        assertEquals(JourneyStatus.IN_PROGRESS, journey.getStatus());
    }

    @Test
    void shouldAdvanceJourneyPosition() {
        DrivingRoute route = createTestRoute();
        JourneyState journey = new JourneyState("journey-1", route, 1000.0); // Fast speed

        journey.start();

        Coordinate initialPosition = journey.getCurrentPosition();
        journey.advance(1.0); // 1 second at 1000 m/s
        Coordinate newPosition = journey.getCurrentPosition();

        // Position should have changed
        assertNotEquals(initialPosition, newPosition);
    }

    @Test
    void shouldCompleteJourneyWhenReachingEnd() {
        DrivingRoute route = createTestRoute();
        JourneyState journey = new JourneyState("journey-1", route, 100000.0); // Very fast

        journey.start();

        // Advance enough to complete the journey
        boolean completed = journey.advance(100.0); // Should cover all waypoints

        assertTrue(completed);
        assertEquals(JourneyStatus.COMPLETED, journey.getStatus());
        assertEquals(100.0, journey.getProgressPercentage(), 0.001);
    }

    @Test
    void shouldNotAdvanceWhenNotStarted() {
        DrivingRoute route = createTestRoute();
        JourneyState journey = new JourneyState("journey-1", route, 10.0);

        Coordinate initialPosition = journey.getCurrentPosition();
        journey.advance(10.0);

        // Position should not change when journey is not started
        assertEquals(initialPosition, journey.getCurrentPosition());
    }

    @Test
    void shouldNotAdvanceWhenPaused() {
        DrivingRoute route = createTestRoute();
        JourneyState journey = new JourneyState("journey-1", route, 1000.0);

        journey.start();
        journey.advance(1.0);
        journey.pause();

        Coordinate positionWhenPaused = journey.getCurrentPosition();
        journey.advance(10.0);

        // Position should not change when paused
        assertEquals(positionWhenPaused, journey.getCurrentPosition());
    }

    @Test
    void shouldUpdateSpeed() {
        DrivingRoute route = createTestRoute();
        JourneyState journey = new JourneyState("journey-1", route, 10.0);

        assertEquals(10.0, journey.getSpeedMetersPerSecond());

        journey.setSpeedMetersPerSecond(20.0);

        assertEquals(20.0, journey.getSpeedMetersPerSecond());
    }

    @Test
    void shouldRejectInvalidSpeed() {
        DrivingRoute route = createTestRoute();
        JourneyState journey = new JourneyState("journey-1", route, 10.0);

        assertThrows(IllegalArgumentException.class, () ->
            journey.setSpeedMetersPerSecond(0));
        assertThrows(IllegalArgumentException.class, () ->
            journey.setSpeedMetersPerSecond(-5));
    }
}

