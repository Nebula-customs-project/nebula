package pse.nebula.worldview.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DrivingRoute Unit Tests")
class DrivingRouteTest {

    private static final Coordinate START = new Coordinate(48.8973, 9.1920);
    private static final Coordinate MIDDLE = new Coordinate(48.8600, 9.1700);
    private static final Coordinate END = new Coordinate(48.8354, 9.1520);

    @Nested
    @DisplayName("Constructor Validation Tests")
    class ConstructorValidationTests {

        @Test
        @DisplayName("Should create valid route with all required fields")
        void shouldCreateValidRoute() {
            List<Coordinate> waypoints = Arrays.asList(START, MIDDLE, END);

            DrivingRoute route = new DrivingRoute(
                    "route-1",
                    "Test Route",
                    "A test description",
                    waypoints,
                    10000.0,
                    600
            );

            assertEquals("route-1", route.id());
            assertEquals("Test Route", route.name());
            assertEquals("A test description", route.description());
            assertEquals(3, route.waypoints().size());
            assertEquals(10000.0, route.totalDistanceMeters());
            assertEquals(600, route.estimatedDurationSeconds());
        }

        @Test
        @DisplayName("Should throw exception for null ID")
        void shouldThrowExceptionForNullId() {
            List<Coordinate> waypoints = Arrays.asList(START, END);

            assertThrows(IllegalArgumentException.class, () ->
                    new DrivingRoute(null, "Name", "Desc", waypoints, 1000, 60));
        }

        @Test
        @DisplayName("Should throw exception for blank ID")
        void shouldThrowExceptionForBlankId() {
            List<Coordinate> waypoints = Arrays.asList(START, END);

            assertThrows(IllegalArgumentException.class, () ->
                    new DrivingRoute("   ", "Name", "Desc", waypoints, 1000, 60));
        }

        @Test
        @DisplayName("Should throw exception for null name")
        void shouldThrowExceptionForNullName() {
            List<Coordinate> waypoints = Arrays.asList(START, END);

            assertThrows(IllegalArgumentException.class, () ->
                    new DrivingRoute("id", null, "Desc", waypoints, 1000, 60));
        }

        @Test
        @DisplayName("Should throw exception for blank name")
        void shouldThrowExceptionForBlankName() {
            List<Coordinate> waypoints = Arrays.asList(START, END);

            assertThrows(IllegalArgumentException.class, () ->
                    new DrivingRoute("id", "", "Desc", waypoints, 1000, 60));
        }

        @Test
        @DisplayName("Should throw exception for null waypoints")
        void shouldThrowExceptionForNullWaypoints() {
            assertThrows(IllegalArgumentException.class, () ->
                    new DrivingRoute("id", "Name", "Desc", null, 1000, 60));
        }

        @Test
        @DisplayName("Should throw exception for empty waypoints")
        void shouldThrowExceptionForEmptyWaypoints() {
            assertThrows(IllegalArgumentException.class, () ->
                    new DrivingRoute("id", "Name", "Desc", Collections.emptyList(), 1000, 60));
        }

        @Test
        @DisplayName("Should throw exception for single waypoint")
        void shouldThrowExceptionForSingleWaypoint() {
            assertThrows(IllegalArgumentException.class, () ->
                    new DrivingRoute("id", "Name", "Desc", Collections.singletonList(START), 1000, 60));
        }

        @Test
        @DisplayName("Should throw exception for waypoints containing null")
        void shouldThrowExceptionForWaypointsContainingNull() {
            List<Coordinate> waypoints = Arrays.asList(START, null, END);

            assertThrows(IllegalArgumentException.class, () ->
                    new DrivingRoute("id", "Name", "Desc", waypoints, 1000, 60));
        }

        @ParameterizedTest
        @DisplayName("Should throw exception for invalid distance")
        @ValueSource(doubles = {0, -1, -100, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY})
        void shouldThrowExceptionForInvalidDistance(double distance) {
            List<Coordinate> waypoints = Arrays.asList(START, END);

            assertThrows(IllegalArgumentException.class, () ->
                    new DrivingRoute("id", "Name", "Desc", waypoints, distance, 60));
        }

        @ParameterizedTest
        @DisplayName("Should throw exception for invalid duration")
        @ValueSource(ints = {0, -1, -100})
        void shouldThrowExceptionForInvalidDuration(int duration) {
            List<Coordinate> waypoints = Arrays.asList(START, END);

            assertThrows(IllegalArgumentException.class, () ->
                    new DrivingRoute("id", "Name", "Desc", waypoints, 1000, duration));
        }
    }

    @Nested
    @DisplayName("Waypoint Access Tests")
    class WaypointAccessTests {

        @Test
        @DisplayName("startPoint should return first waypoint")
        void startPointShouldReturnFirstWaypoint() {
            List<Coordinate> waypoints = Arrays.asList(START, MIDDLE, END);
            DrivingRoute route = new DrivingRoute("id", "Name", "Desc", waypoints, 1000, 60);

            assertEquals(START, route.startPoint());
        }

        @Test
        @DisplayName("endPoint should return last waypoint")
        void endPointShouldReturnLastWaypoint() {
            List<Coordinate> waypoints = Arrays.asList(START, MIDDLE, END);
            DrivingRoute route = new DrivingRoute("id", "Name", "Desc", waypoints, 1000, 60);

            assertEquals(END, route.endPoint());
        }

        @Test
        @DisplayName("getTotalWaypoints should return correct count")
        void getTotalWaypointsShouldReturnCorrectCount() {
            List<Coordinate> waypoints = Arrays.asList(START, MIDDLE, END);
            DrivingRoute route = new DrivingRoute("id", "Name", "Desc", waypoints, 1000, 60);

            assertEquals(3, route.getTotalWaypoints());
        }

        @Test
        @DisplayName("getWaypointAt should return correct waypoint")
        void getWaypointAtShouldReturnCorrectWaypoint() {
            List<Coordinate> waypoints = Arrays.asList(START, MIDDLE, END);
            DrivingRoute route = new DrivingRoute("id", "Name", "Desc", waypoints, 1000, 60);

            assertEquals(START, route.getWaypointAt(0));
            assertEquals(MIDDLE, route.getWaypointAt(1));
            assertEquals(END, route.getWaypointAt(2));
        }

        @Test
        @DisplayName("getWaypointAt should throw for negative index")
        void getWaypointAtShouldThrowForNegativeIndex() {
            List<Coordinate> waypoints = Arrays.asList(START, END);
            DrivingRoute route = new DrivingRoute("id", "Name", "Desc", waypoints, 1000, 60);

            assertThrows(IndexOutOfBoundsException.class, () -> route.getWaypointAt(-1));
        }

        @Test
        @DisplayName("getWaypointAt should throw for index out of bounds")
        void getWaypointAtShouldThrowForIndexOutOfBounds() {
            List<Coordinate> waypoints = Arrays.asList(START, END);
            DrivingRoute route = new DrivingRoute("id", "Name", "Desc", waypoints, 1000, 60);

            assertThrows(IndexOutOfBoundsException.class, () -> route.getWaypointAt(5));
        }
    }

    @Nested
    @DisplayName("Immutability Tests")
    class ImmutabilityTests {

        @Test
        @DisplayName("Waypoints list should be immutable")
        void waypointsListShouldBeImmutable() {
            List<Coordinate> waypoints = Arrays.asList(START, END);
            DrivingRoute route = new DrivingRoute("id", "Name", "Desc", waypoints, 1000, 60);

            List<Coordinate> returnedWaypoints = route.waypoints();

            assertThrows(UnsupportedOperationException.class, () ->
                    returnedWaypoints.add(MIDDLE));
        }
    }
}

