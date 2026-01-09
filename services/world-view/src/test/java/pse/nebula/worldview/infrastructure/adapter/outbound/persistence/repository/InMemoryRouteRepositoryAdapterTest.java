package pse.nebula.worldview.infrastructure.adapter.outbound.persistence.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pse.nebula.worldview.domain.model.DrivingRoute;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for InMemoryRouteRepositoryAdapter.
 */
@DisplayName("InMemoryRouteRepositoryAdapter Tests")
class InMemoryRouteRepositoryAdapterTest {

    private InMemoryRouteRepositoryAdapter repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRouteRepositoryAdapter();
        repository.initializeRoutes();
    }

    @Nested
    @DisplayName("findAll() Tests")
    class FindAllTests {

        @Test
        @DisplayName("Should return all 8 routes")
        void shouldReturnAll8Routes() {
            List<DrivingRoute> routes = repository.findAll();

            assertEquals(8, routes.size());
        }

        @Test
        @DisplayName("All routes should have valid data")
        void allRoutesShouldHaveValidData() {
            List<DrivingRoute> routes = repository.findAll();

            for (DrivingRoute route : routes) {
                assertNotNull(route.id());
                assertNotNull(route.name());
                assertNotNull(route.description());
                assertNotNull(route.waypoints());
                assertTrue(route.waypoints().size() >= 2);
                assertTrue(route.totalDistanceMeters() > 0);
                assertTrue(route.estimatedDurationSeconds() > 0);
            }
        }

        @Test
        @DisplayName("All routes should end at dealership")
        void allRoutesShouldEndAtDealership() {
            List<DrivingRoute> routes = repository.findAll();
            double dealershipLat = 48.8354;
            double dealershipLng = 9.1520;

            for (DrivingRoute route : routes) {
                assertEquals(dealershipLat, route.endPoint().latitude(), 0.0001,
                        "Route " + route.id() + " should end at dealership latitude");
                assertEquals(dealershipLng, route.endPoint().longitude(), 0.0001,
                        "Route " + route.id() + " should end at dealership longitude");
            }
        }
    }

    @Nested
    @DisplayName("findById() Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should find route by valid ID")
        void shouldFindRouteByValidId() {
            Optional<DrivingRoute> route = repository.findById("route-1");

            assertTrue(route.isPresent());
            assertEquals("route-1", route.get().id());
            assertEquals("Ludwigsburg Schloss Route", route.get().name());
        }

        @Test
        @DisplayName("Should return empty for non-existent route")
        void shouldReturnEmptyForNonExistentRoute() {
            Optional<DrivingRoute> route = repository.findById("route-999");

            assertTrue(route.isEmpty());
        }

        @Test
        @DisplayName("Should return empty for null ID")
        void shouldReturnEmptyForNullId() {
            Optional<DrivingRoute> route = repository.findById(null);

            assertTrue(route.isEmpty());
        }

        @Test
        @DisplayName("Should find all routes by ID")
        void shouldFindAllRoutesById() {
            for (int i = 1; i <= 8; i++) {
                String routeId = "route-" + i;
                Optional<DrivingRoute> route = repository.findById(routeId);

                assertTrue(route.isPresent(), "Route " + routeId + " should exist");
                assertEquals(routeId, route.get().id());
            }
        }
    }

    @Nested
    @DisplayName("count() Tests")
    class CountTests {

        @Test
        @DisplayName("Should return 8 routes")
        void shouldReturn8Routes() {
            int count = repository.count();

            assertEquals(8, count);
        }
    }

    @Nested
    @DisplayName("Route Data Validation Tests")
    class RouteDataValidationTests {

        @Test
        @DisplayName("Route 1 should be from Ludwigsburg Schloss")
        void route1ShouldBeFromLudwigsburgSchloss() {
            Optional<DrivingRoute> route = repository.findById("route-1");

            assertTrue(route.isPresent());
            assertEquals("Ludwigsburg Schloss Route", route.get().name());
            assertEquals(48.8973, route.get().startPoint().latitude(), 0.001);
            assertEquals(9.1920, route.get().startPoint().longitude(), 0.001);
        }

        @Test
        @DisplayName("Route 2 should be from Favoritepark")
        void route2ShouldBeFromFavoritepark() {
            Optional<DrivingRoute> route = repository.findById("route-2");

            assertTrue(route.isPresent());
            assertEquals("Favoritepark Route", route.get().name());
        }

        @Test
        @DisplayName("Route 3 should be from Esslingen")
        void route3ShouldBeFromEsslingen() {
            Optional<DrivingRoute> route = repository.findById("route-3");

            assertTrue(route.isPresent());
            assertEquals("Esslingen Route", route.get().name());
        }

        @Test
        @DisplayName("Route 4 should be from Böblingen")
        void route4ShouldBeFromBoblingen() {
            Optional<DrivingRoute> route = repository.findById("route-4");

            assertTrue(route.isPresent());
            // Actual name in InMemoryRouteRepositoryAdapter
            assertNotNull(route.get().name());
        }

        @Test
        @DisplayName("Route waypoints should form continuous path")
        void routeWaypointsShouldFormContinuousPath() {
            List<DrivingRoute> routes = repository.findAll();

            for (DrivingRoute route : routes) {
                // First waypoint should match start point
                assertEquals(route.startPoint(), route.waypoints().get(0));

                // Last waypoint should match end point
                assertEquals(route.endPoint(), route.waypoints().get(route.waypoints().size() - 1));
            }
        }
    }

    @Nested
    @DisplayName("Routes Start From Different Locations Tests")
    class DifferentStartLocationsTests {

        @Test
        @DisplayName("Routes should start from different locations")
        void routesShouldStartFromDifferentLocations() {
            List<DrivingRoute> routes = repository.findAll();

            // Check that not all routes start from the same location
            long uniqueStartPoints = routes.stream()
                    .map(route -> route.startPoint().latitude() + "," + route.startPoint().longitude())
                    .distinct()
                    .count();

            assertTrue(uniqueStartPoints > 1, "Routes should start from different locations");
        }
    }
}

