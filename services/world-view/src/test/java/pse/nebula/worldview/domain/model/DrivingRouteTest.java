package pse.nebula.worldview.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DrivingRoute.
 */
@DisplayName("DrivingRoute Tests")
class DrivingRouteTest {

    @Nested
    @DisplayName("Constructor Validation Tests")
    class ConstructorValidationTests {

        @Test
        @DisplayName("Should create valid route")
        void shouldCreateValidRoute() {
            List<Coordinate> waypoints = Arrays.asList(
                    new Coordinate(48.8973, 9.1920),
                    new Coordinate(48.8354, 9.1520)
            );

            DrivingRoute route = new DrivingRoute(
                    "route-1",
                    "Test Route",
                    "A test route description",
                    waypoints,
                    5000.0,
                    600
            );

            assertEquals("route-1", route.id());
            assertEquals("Test Route", route.name());
            assertEquals("A test route description", route.description());
            assertEquals(2, route.waypoints().size());
            assertEquals(5000.0, route.totalDistanceMeters());
            assertEquals(600, route.estimatedDurationSeconds());
        }

        @Test
        @DisplayName("Should throw exception for null ID")
        void shouldThrowExceptionForNullId() {
            List<Coordinate> waypoints = Arrays.asList(
                    new Coordinate(48.8973, 9.1920),
                    new Coordinate(48.8354, 9.1520)
            );

            assertThrows(IllegalArgumentException.class, () ->
                    new DrivingRoute(null, "Test", "Desc", waypoints, 1000, 60));
        }

        @Test
        @DisplayName("Should throw exception for empty ID")
        void shouldThrowExceptionForEmptyId() {
            List<Coordinate> waypoints = Arrays.asList(
                    new Coordinate(48.8973, 9.1920),
                    new Coordinate(48.8354, 9.1520)
            );

            assertThrows(IllegalArgumentException.class, () ->
                    new DrivingRoute("", "Test", "Desc", waypoints, 1000, 60));
        }

        @Test
        @DisplayName("Should throw exception for blank ID")
        void shouldThrowExceptionForBlankId() {
            List<Coordinate> waypoints = Arrays.asList(
                    new Coordinate(48.8973, 9.1920),
                    new Coordinate(48.8354, 9.1520)
            );

            assertThrows(IllegalArgumentException.class, () ->
                    new DrivingRoute("   ", "Test", "Desc", waypoints, 1000, 60));
        }

        @Test
        @DisplayName("Should throw exception for null name")
        void shouldThrowExceptionForNullName() {
            List<Coordinate> waypoints = Arrays.asList(
                    new Coordinate(48.8973, 9.1920),
                    new Coordinate(48.8354, 9.1520)
            );

            assertThrows(IllegalArgumentException.class, () ->
                    new DrivingRoute("route-1", null, "Desc", waypoints, 1000, 60));
        }

        @Test
        @DisplayName("Should throw exception for empty name")
        void shouldThrowExceptionForEmptyName() {
            List<Coordinate> waypoints = Arrays.asList(
                    new Coordinate(48.8973, 9.1920),
                    new Coordinate(48.8354, 9.1520)
            );

            assertThrows(IllegalArgumentException.class, () ->
                    new DrivingRoute("route-1", "", "Desc", waypoints, 1000, 60));
        }

        @Test
        @DisplayName("Should throw exception for null waypoints")
        void shouldThrowExceptionForNullWaypoints() {
            assertThrows(IllegalArgumentException.class, () ->
                    new DrivingRoute("route-1", "Test", "Desc", null, 1000, 60));
        }

        @Test
        @DisplayName("Should throw exception for less than 2 waypoints")
        void shouldThrowExceptionForLessThan2Waypoints() {
            List<Coordinate> waypoints = Collections.singletonList(
                    new Coordinate(48.8973, 9.1920)
            );

            assertThrows(IllegalArgumentException.class, () ->
                    new DrivingRoute("route-1", "Test", "Desc", waypoints, 1000, 60));
        }

        @Test
        @DisplayName("Should throw exception for empty waypoints")
        void shouldThrowExceptionForEmptyWaypoints() {
            List<Coordinate> waypoints = Collections.emptyList();

            assertThrows(IllegalArgumentException.class, () ->
                    new DrivingRoute("route-1", "Test", "Desc", waypoints, 1000, 60));
        }

        @Test
        @DisplayName("Should throw exception for waypoints with null element")
        void shouldThrowExceptionForWaypointsWithNullElement() {
            List<Coordinate> waypoints = Arrays.asList(
                    new Coordinate(48.8973, 9.1920),
                    null,
                    new Coordinate(48.8354, 9.1520)
            );

            assertThrows(IllegalArgumentException.class, () ->
                    new DrivingRoute("route-1", "Test", "Desc", waypoints, 1000, 60));
        }

        @Test
        @DisplayName("Should throw exception for zero distance")
        void shouldThrowExceptionForZeroDistance() {
            List<Coordinate> waypoints = Arrays.asList(
                    new Coordinate(48.8973, 9.1920),
                    new Coordinate(48.8354, 9.1520)
            );

            assertThrows(IllegalArgumentException.class, () ->
                    new DrivingRoute("route-1", "Test", "Desc", waypoints, 0, 60));
        }

        @Test
        @DisplayName("Should throw exception for negative distance")
        void shouldThrowExceptionForNegativeDistance() {
            List<Coordinate> waypoints = Arrays.asList(
                    new Coordinate(48.8973, 9.1920),
                    new Coordinate(48.8354, 9.1520)
            );

            assertThrows(IllegalArgumentException.class, () ->
                    new DrivingRoute("route-1", "Test", "Desc", waypoints, -1000, 60));
        }

        @Test
        @DisplayName("Should throw exception for NaN distance")
        void shouldThrowExceptionForNaNDistance() {
            List<Coordinate> waypoints = Arrays.asList(
                    new Coordinate(48.8973, 9.1920),
                    new Coordinate(48.8354, 9.1520)
            );

            assertThrows(IllegalArgumentException.class, () ->
                    new DrivingRoute("route-1", "Test", "Desc", waypoints, Double.NaN, 60));
        }

        @Test
        @DisplayName("Should throw exception for infinite distance")
        void shouldThrowExceptionForInfiniteDistance() {
            List<Coordinate> waypoints = Arrays.asList(
                    new Coordinate(48.8973, 9.1920),
                    new Coordinate(48.8354, 9.1520)
            );

            assertThrows(IllegalArgumentException.class, () ->
                    new DrivingRoute("route-1", "Test", "Desc", waypoints, Double.POSITIVE_INFINITY, 60));
        }

        @Test
        @DisplayName("Should throw exception for zero duration")
        void shouldThrowExceptionForZeroDuration() {
            List<Coordinate> waypoints = Arrays.asList(
                    new Coordinate(48.8973, 9.1920),
                    new Coordinate(48.8354, 9.1520)
            );

            assertThrows(IllegalArgumentException.class, () ->
                    new DrivingRoute("route-1", "Test", "Desc", waypoints, 1000, 0));
        }

        @Test
        @DisplayName("Should throw exception for negative duration")
        void shouldThrowExceptionForNegativeDuration() {
            List<Coordinate> waypoints = Arrays.asList(
                    new Coordinate(48.8973, 9.1920),
                    new Coordinate(48.8354, 9.1520)
            );

            assertThrows(IllegalArgumentException.class, () ->
                    new DrivingRoute("route-1", "Test", "Desc", waypoints, 1000, -60));
        }
    }

    @Nested
    @DisplayName("Waypoints Immutability Tests")
    class WaypointsImmutabilityTests {

        @Test
        @DisplayName("Waypoints list should be immutable")
        void waypointsListShouldBeImmutable() {
            List<Coordinate> waypoints = Arrays.asList(
                    new Coordinate(48.8973, 9.1920),
                    new Coordinate(48.8354, 9.1520)
            );

            DrivingRoute route = new DrivingRoute(
                    "route-1", "Test", "Desc", waypoints, 1000, 60
            );

            List<Coordinate> routeWaypoints = route.waypoints();
            Coordinate newCoordinate = new Coordinate(48.8, 9.1);

            assertThrows(UnsupportedOperationException.class, () -> routeWaypoints.add(newCoordinate));
        }
    }

    @Nested
    @DisplayName("startPoint() Tests")
    class StartPointTests {

        @Test
        @DisplayName("Should return first waypoint as start point")
        void shouldReturnFirstWaypointAsStartPoint() {
            Coordinate start = new Coordinate(48.8973, 9.1920);
            Coordinate end = new Coordinate(48.8354, 9.1520);
            List<Coordinate> waypoints = Arrays.asList(start, end);

            DrivingRoute route = new DrivingRoute(
                    "route-1", "Test", "Desc", waypoints, 1000, 60
            );

            assertEquals(start, route.startPoint());
        }
    }

    @Nested
    @DisplayName("endPoint() Tests")
    class EndPointTests {

        @Test
        @DisplayName("Should return last waypoint as end point")
        void shouldReturnLastWaypointAsEndPoint() {
            Coordinate start = new Coordinate(48.8973, 9.1920);
            Coordinate middle = new Coordinate(48.86, 9.17);
            Coordinate end = new Coordinate(48.8354, 9.1520);
            List<Coordinate> waypoints = Arrays.asList(start, middle, end);

            DrivingRoute route = new DrivingRoute(
                    "route-1", "Test", "Desc", waypoints, 1000, 60
            );

            assertEquals(end, route.endPoint());
        }
    }

    @Nested
    @DisplayName("getTotalWaypoints() Tests")
    class GetTotalWaypointsTests {

        @Test
        @DisplayName("Should return correct waypoint count")
        void shouldReturnCorrectWaypointCount() {
            List<Coordinate> waypoints = Arrays.asList(
                    new Coordinate(48.8973, 9.1920),
                    new Coordinate(48.86, 9.17),
                    new Coordinate(48.85, 9.16),
                    new Coordinate(48.8354, 9.1520)
            );

            DrivingRoute route = new DrivingRoute(
                    "route-1", "Test", "Desc", waypoints, 1000, 60
            );

            assertEquals(4, route.getTotalWaypoints());
        }
    }

    @Nested
    @DisplayName("getWaypointAt() Tests")
    class GetWaypointAtTests {

        @Test
        @DisplayName("Should return waypoint at valid index")
        void shouldReturnWaypointAtValidIndex() {
            Coordinate coord0 = new Coordinate(48.8973, 9.1920);
            Coordinate coord1 = new Coordinate(48.86, 9.17);
            Coordinate coord2 = new Coordinate(48.8354, 9.1520);
            List<Coordinate> waypoints = Arrays.asList(coord0, coord1, coord2);

            DrivingRoute route = new DrivingRoute(
                    "route-1", "Test", "Desc", waypoints, 1000, 60
            );

            assertEquals(coord0, route.getWaypointAt(0));
            assertEquals(coord1, route.getWaypointAt(1));
            assertEquals(coord2, route.getWaypointAt(2));
        }

        @Test
        @DisplayName("Should throw exception for negative index")
        void shouldThrowExceptionForNegativeIndex() {
            List<Coordinate> waypoints = Arrays.asList(
                    new Coordinate(48.8973, 9.1920),
                    new Coordinate(48.8354, 9.1520)
            );

            DrivingRoute route = new DrivingRoute(
                    "route-1", "Test", "Desc", waypoints, 1000, 60
            );

            assertThrows(IndexOutOfBoundsException.class, () ->
                    route.getWaypointAt(-1));
        }

        @Test
        @DisplayName("Should throw exception for index out of bounds")
        void shouldThrowExceptionForIndexOutOfBounds() {
            List<Coordinate> waypoints = Arrays.asList(
                    new Coordinate(48.8973, 9.1920),
                    new Coordinate(48.8354, 9.1520)
            );

            DrivingRoute route = new DrivingRoute(
                    "route-1", "Test", "Desc", waypoints, 1000, 60
            );

            assertThrows(IndexOutOfBoundsException.class, () ->
                    route.getWaypointAt(2));
        }
    }
}