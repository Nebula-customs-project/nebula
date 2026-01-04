package pse.nebula.worldview.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CoordinateTest {

    @Test
    void shouldCreateValidCoordinate() {
        Coordinate coord = new Coordinate(48.8354, 9.1520);

        assertEquals(48.8354, coord.latitude());
        assertEquals(9.1520, coord.longitude());
    }

    @Test
    void shouldRejectInvalidLatitude() {
        assertThrows(IllegalArgumentException.class, () ->
            new Coordinate(91, 0));
        assertThrows(IllegalArgumentException.class, () ->
            new Coordinate(-91, 0));
    }

    @Test
    void shouldRejectInvalidLongitude() {
        assertThrows(IllegalArgumentException.class, () ->
            new Coordinate(0, 181));
        assertThrows(IllegalArgumentException.class, () ->
            new Coordinate(0, -181));
    }

    @Test
    void shouldCalculateDistance() {
        // Ludwigsburg Schloss to Dealership
        Coordinate start = new Coordinate(48.8973, 9.1920);
        Coordinate end = new Coordinate(48.8354, 9.1520);

        double distance = start.distanceTo(end);

        // Approximately 7-8 km straight line distance
        assertTrue(distance > 5000 && distance < 10000,
            "Distance should be roughly 7km, was: " + distance);
    }

    @Test
    void shouldInterpolateBetweenCoordinates() {
        Coordinate start = new Coordinate(48.8, 9.0);
        Coordinate end = new Coordinate(49.0, 10.0);

        Coordinate midpoint = start.interpolateTo(end, 0.5);

        assertEquals(48.9, midpoint.latitude(), 0.0001);
        assertEquals(9.5, midpoint.longitude(), 0.0001);
    }

    @Test
    void interpolateWithZeroFractionReturnsStart() {
        Coordinate start = new Coordinate(48.8, 9.0);
        Coordinate end = new Coordinate(49.0, 10.0);

        Coordinate result = start.interpolateTo(end, 0);

        assertEquals(start.latitude(), result.latitude(), 0.0001);
        assertEquals(start.longitude(), result.longitude(), 0.0001);
    }

    @Test
    void interpolateWithOneFractionReturnsEnd() {
        Coordinate start = new Coordinate(48.8, 9.0);
        Coordinate end = new Coordinate(49.0, 10.0);

        Coordinate result = start.interpolateTo(end, 1);

        assertEquals(end.latitude(), result.latitude(), 0.0001);
        assertEquals(end.longitude(), result.longitude(), 0.0001);
    }
}

