package pse.nebula.worldview.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

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

    @ParameterizedTest(name = "Should reject invalid journey ID: \"{0}\"")
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void shouldRejectInvalidJourneyId(String invalidJourneyId) {
        DrivingRoute route = createTestRoute();
        assertThrows(IllegalArgumentException.class, () ->
            new JourneyState(invalidJourneyId, route, 10.0));
    }

    @Test
    void shouldRejectNullRoute() {
        assertThrows(IllegalArgumentException.class, () ->
            new JourneyState("journey-1", null, 10.0));
    }

    @Test
    void shouldRejectZeroSpeed() {
        DrivingRoute route = createTestRoute();
        assertThrows(IllegalArgumentException.class, () ->
            new JourneyState("journey-1", route, 0));
    }

    @Test
    void shouldRejectNegativeSpeed() {
        DrivingRoute route = createTestRoute();
        assertThrows(IllegalArgumentException.class, () ->
            new JourneyState("journey-1", route, -10.0));
    }

    @Test
    void shouldRejectNaNSpeed() {
        DrivingRoute route = createTestRoute();
        assertThrows(IllegalArgumentException.class, () ->
            new JourneyState("journey-1", route, Double.NaN));
    }

    @Test
    void shouldRejectInfiniteSpeed() {
        DrivingRoute route = createTestRoute();
        assertThrows(IllegalArgumentException.class, () ->
            new JourneyState("journey-1", route, Double.POSITIVE_INFINITY));
    }

    @Test
    void shouldNotStartCompletedJourney() {
        DrivingRoute route = createTestRoute();
        JourneyState journey = new JourneyState("journey-1", route, 100000.0);
        journey.start();
        journey.advance(100.0); // Complete the journey

        assertEquals(JourneyStatus.COMPLETED, journey.getStatus());
        assertThrows(IllegalStateException.class, journey::start);
    }

    @Test
    void shouldNotStartAlreadyStartedJourney() {
        DrivingRoute route = createTestRoute();
        JourneyState journey = new JourneyState("journey-1", route, 10.0);
        journey.start();

        assertThrows(IllegalStateException.class, journey::start);
    }

    @Test
    void shouldNotPauseNotStartedJourney() {
        DrivingRoute route = createTestRoute();
        JourneyState journey = new JourneyState("journey-1", route, 10.0);

        assertThrows(IllegalStateException.class, journey::pause);
    }

    @Test
    void shouldNotResumeNotPausedJourney() {
        DrivingRoute route = createTestRoute();
        JourneyState journey = new JourneyState("journey-1", route, 10.0);
        journey.start();

        assertThrows(IllegalStateException.class, journey::resume);
    }

    @Test
    void shouldNotChangeSpeedOfCompletedJourney() {
        DrivingRoute route = createTestRoute();
        JourneyState journey = new JourneyState("journey-1", route, 100000.0);
        journey.start();
        journey.advance(100.0); // Complete the journey

        assertThrows(IllegalStateException.class, () ->
            journey.setSpeedMetersPerSecond(20.0));
    }

    @Test
    void shouldRejectNaNSpeedUpdate() {
        DrivingRoute route = createTestRoute();
        JourneyState journey = new JourneyState("journey-1", route, 10.0);

        assertThrows(IllegalArgumentException.class, () ->
            journey.setSpeedMetersPerSecond(Double.NaN));
    }

    @Test
    void shouldRejectInfiniteSpeedUpdate() {
        DrivingRoute route = createTestRoute();
        JourneyState journey = new JourneyState("journey-1", route, 10.0);

        assertThrows(IllegalArgumentException.class, () ->
            journey.setSpeedMetersPerSecond(Double.POSITIVE_INFINITY));
    }

    @Test
    void shouldRejectNegativeElapsedTime() {
        DrivingRoute route = createTestRoute();
        JourneyState journey = new JourneyState("journey-1", route, 10.0);
        journey.start();

        assertThrows(IllegalArgumentException.class, () ->
            journey.advance(-1.0));
    }

    @Test
    void shouldRejectZeroElapsedTime() {
        DrivingRoute route = createTestRoute();
        JourneyState journey = new JourneyState("journey-1", route, 10.0);
        journey.start();

        assertThrows(IllegalArgumentException.class, () ->
            journey.advance(0));
    }

    @Test
    void shouldRejectNaNElapsedTime() {
        DrivingRoute route = createTestRoute();
        JourneyState journey = new JourneyState("journey-1", route, 10.0);
        journey.start();

        assertThrows(IllegalArgumentException.class, () ->
            journey.advance(Double.NaN));
    }

    @Test
    void shouldReturnTrueWhenAlreadyCompleted() {
        DrivingRoute route = createTestRoute();
        JourneyState journey = new JourneyState("journey-1", route, 100000.0);
        journey.start();
        journey.advance(100.0); // Complete

        boolean result = journey.advance(1.0);

        assertTrue(result);
        assertEquals(JourneyStatus.COMPLETED, journey.getStatus());
    }

    @Test
    void shouldTrackProgressPercentage() {
        DrivingRoute route = createTestRoute();
        JourneyState journey = new JourneyState("journey-1", route, 1000.0);
        journey.start();

        assertEquals(0.0, journey.getProgressPercentage(), 0.1);

        journey.advance(5.0); // Advance a bit

        assertTrue(journey.getProgressPercentage() > 0);
        assertTrue(journey.getProgressPercentage() < 100);
    }

    @Test
    void shouldGetRoute() {
        DrivingRoute route = createTestRoute();
        JourneyState journey = new JourneyState("journey-1", route, 10.0);

        assertEquals(route, journey.getRoute());
    }

    @Test
    void shouldGetJourneyId() {
        DrivingRoute route = createTestRoute();
        JourneyState journey = new JourneyState("journey-1", route, 10.0);

        assertEquals("journey-1", journey.getJourneyId());
    }
}