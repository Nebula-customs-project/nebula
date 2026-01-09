package pse.nebula.worldview.infrastructure.adapter.outbound.persistence.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pse.nebula.worldview.domain.model.Coordinate;
import pse.nebula.worldview.domain.model.DrivingRoute;
import pse.nebula.worldview.domain.port.outbound.RouteRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for JpaRouteRepositoryAdapter.
 * Uses H2 in-memory database for testing.
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("JpaRouteRepositoryAdapter Integration Tests")
class JpaRouteRepositoryAdapterTest {

    @Autowired
    private RouteRepository routeRepository;

    // Dealership - Destination for all routes
    private static final double DEALERSHIP_LAT = 48.8354;
    private static final double DEALERSHIP_LNG = 9.1520;

    @Nested
    @DisplayName("Route Initialization Tests")
    class RouteInitializationTests {

        @Test
        @DisplayName("Should initialize exactly 8 routes")
        void shouldInitialize8Routes() {
            assertEquals(8, routeRepository.count());
        }

        @Test
        @DisplayName("All routes should have valid IDs")
        void allRoutesShouldHaveValidIds() {
            List<DrivingRoute> routes = routeRepository.findAll();

            for (int i = 1; i <= 8; i++) {
                String expectedId = "route-" + i;
                assertTrue(routes.stream().anyMatch(r -> r.id().equals(expectedId)),
                        "Should have route with ID: " + expectedId);
            }
        }

        @Test
        @DisplayName("All routes should have non-empty names")
        void allRoutesShouldHaveNames() {
            List<DrivingRoute> routes = routeRepository.findAll();

            for (DrivingRoute route : routes) {
                assertNotNull(route.name());
                assertFalse(route.name().isBlank());
            }
        }

        @Test
        @DisplayName("All routes should have descriptions")
        void allRoutesShouldHaveDescriptions() {
            List<DrivingRoute> routes = routeRepository.findAll();

            for (DrivingRoute route : routes) {
                assertNotNull(route.description());
                assertFalse(route.description().isBlank());
            }
        }
    }

    @Nested
    @DisplayName("Route Endpoint Tests")
    class RouteEndpointTests {

        @Test
        @DisplayName("All routes should end at Dealership")
        void allRoutesShouldEndAtDealership() {
            List<DrivingRoute> routes = routeRepository.findAll();

            for (DrivingRoute route : routes) {
                assertEquals(DEALERSHIP_LAT, route.endPoint().latitude(), 0.0001,
                        "Route " + route.id() + " should end at dealership latitude");
                assertEquals(DEALERSHIP_LNG, route.endPoint().longitude(), 0.0001,
                        "Route " + route.id() + " should end at dealership longitude");
            }
        }

        @Test
        @DisplayName("All routes should have unique starting points")
        void allRoutesShouldHaveUniqueStartingPoints() {
            List<DrivingRoute> routes = routeRepository.findAll();

            long uniqueStarts = routes.stream()
                    .map(r -> r.startPoint().latitude() + "," + r.startPoint().longitude())
                    .distinct()
                    .count();

            assertEquals(8, uniqueStarts, "All 8 routes should have unique starting points");
        }

        @Test
        @DisplayName("Routes should start from greater Stuttgart region")
        void routesShouldStartFromStuttgartArea() {
            List<DrivingRoute> routes = routeRepository.findAll();

            // Greater Stuttgart region bounds (including Reutlingen, Böblingen, etc.)
            double minLat = 48.4;  // Extended south for Reutlingen
            double maxLat = 49.0;
            double minLng = 8.9;
            double maxLng = 9.4;

            for (DrivingRoute route : routes) {
                Coordinate start = route.startPoint();
                assertTrue(start.latitude() >= minLat && start.latitude() <= maxLat,
                        "Route " + route.id() + " start latitude should be in Stuttgart region");
                assertTrue(start.longitude() >= minLng && start.longitude() <= maxLng,
                        "Route " + route.id() + " start longitude should be in Stuttgart region");
            }
        }
    }

    @Nested
    @DisplayName("Route Waypoint Tests")
    class RouteWaypointTests {

        @Test
        @DisplayName("All routes should have at least 2 waypoints")
        void allRoutesShouldHaveMinimumWaypoints() {
            List<DrivingRoute> routes = routeRepository.findAll();

            for (DrivingRoute route : routes) {
                assertTrue(route.waypoints().size() >= 2,
                        "Route " + route.id() + " should have at least 2 waypoints");
            }
        }

        @Test
        @DisplayName("First waypoint should match start point")
        void firstWaypointShouldMatchStartPoint() {
            List<DrivingRoute> routes = routeRepository.findAll();

            for (DrivingRoute route : routes) {
                Coordinate firstWaypoint = route.waypoints().get(0);
                assertEquals(route.startPoint().latitude(), firstWaypoint.latitude(), 0.0001,
                        "Route " + route.id() + " first waypoint should match start point latitude");
                assertEquals(route.startPoint().longitude(), firstWaypoint.longitude(), 0.0001,
                        "Route " + route.id() + " first waypoint should match start point longitude");
            }
        }

        @Test
        @DisplayName("Last waypoint should match end point")
        void lastWaypointShouldMatchEndPoint() {
            List<DrivingRoute> routes = routeRepository.findAll();

            for (DrivingRoute route : routes) {
                Coordinate lastWaypoint = route.waypoints().get(route.waypoints().size() - 1);
                assertEquals(route.endPoint().latitude(), lastWaypoint.latitude(), 0.0001,
                        "Route " + route.id() + " last waypoint should match end point latitude");
                assertEquals(route.endPoint().longitude(), lastWaypoint.longitude(), 0.0001,
                        "Route " + route.id() + " last waypoint should match end point longitude");
            }
        }

        @Test
        @DisplayName("Waypoints should be in correct order (sequence)")
        void waypointsShouldBeInCorrectOrder() {
            List<DrivingRoute> routes = routeRepository.findAll();

            for (DrivingRoute route : routes) {
                // Verify we can iterate through waypoints in sequence
                for (int i = 0; i < route.getTotalWaypoints(); i++) {
                    Coordinate waypoint = route.getWaypointAt(i);
                    assertNotNull(waypoint, "Waypoint at index " + i + " should exist");
                }
            }
        }
    }

    @Nested
    @DisplayName("Route Distance and Duration Tests")
    class RouteDistanceDurationTests {

        @Test
        @DisplayName("All routes should have positive distance")
        void allRoutesShouldHavePositiveDistance() {
            List<DrivingRoute> routes = routeRepository.findAll();

            for (DrivingRoute route : routes) {
                assertTrue(route.totalDistanceMeters() > 0,
                        "Route " + route.id() + " should have positive distance");
            }
        }

        @Test
        @DisplayName("All routes should have positive duration")
        void allRoutesShouldHavePositiveDuration() {
            List<DrivingRoute> routes = routeRepository.findAll();

            for (DrivingRoute route : routes) {
                assertTrue(route.estimatedDurationSeconds() > 0,
                        "Route " + route.id() + " should have positive duration");
            }
        }
    }

    @Nested
    @DisplayName("findById Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should find route by ID")
        void shouldFindRouteById() {
            Optional<DrivingRoute> result = routeRepository.findById("route-1");

            assertTrue(result.isPresent());
            assertEquals("route-1", result.get().id());
            assertEquals("Ludwigsburg Schloss Route", result.get().name());
        }

        @Test
        @DisplayName("Should return empty for non-existent route")
        void shouldReturnEmptyForNonExistentRoute() {
            Optional<DrivingRoute> result = routeRepository.findById("non-existent");

            assertFalse(result.isPresent());
        }

        @Test
        @DisplayName("Should find all routes by ID")
        void shouldFindAllRoutesById() {
            for (int i = 1; i <= 8; i++) {
                String routeId = "route-" + i;
                Optional<DrivingRoute> result = routeRepository.findById(routeId);
                assertTrue(result.isPresent(), "Should find route: " + routeId);
                assertEquals(routeId, result.get().id());
            }
        }
    }

    @Nested
    @DisplayName("Specific Route Tests")
    class SpecificRouteTests {

        @Test
        @DisplayName("Route 1 should be from Ludwigsburg Schloss")
        void route1ShouldBeFromLudwigsburg() {
            DrivingRoute route = routeRepository.findById("route-1").orElseThrow();

            assertEquals("Ludwigsburg Schloss Route", route.name());
            // Ludwigsburg Schloss coordinates (with tolerance)
            assertTrue(route.startPoint().latitude() >= 48.89 && route.startPoint().latitude() <= 48.91,
                    "Route 1 should start near Ludwigsburg");
        }

        @Test
        @DisplayName("Route 2 should be from Favoritepark")
        void route2ShouldBeFromFavoritepark() {
            DrivingRoute route = routeRepository.findById("route-2").orElseThrow();

            assertEquals("Favoritepark Route", route.name());
        }
    }
}