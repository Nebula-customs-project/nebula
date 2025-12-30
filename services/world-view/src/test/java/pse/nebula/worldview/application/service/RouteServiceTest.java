package pse.nebula.worldview.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pse.nebula.worldview.domain.exception.RouteNotFoundException;
import pse.nebula.worldview.domain.model.Coordinate;
import pse.nebula.worldview.domain.model.DrivingRoute;
import pse.nebula.worldview.domain.port.outbound.RouteRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RouteService Unit Tests")
class RouteServiceTest {

    @Mock
    private RouteRepository routeRepository;

    @InjectMocks
    private RouteService routeService;

    private DrivingRoute testRoute1;
    private DrivingRoute testRoute2;

    @BeforeEach
    void setUp() {
        Coordinate start1 = new Coordinate(48.8973, 9.1920);
        Coordinate end = new Coordinate(48.8354, 9.1520);
        List<Coordinate> waypoints1 = Arrays.asList(start1, end);

        testRoute1 = new DrivingRoute(
                "route-1",
                "Test Route 1",
                "Test description 1",
                start1,
                end,
                waypoints1,
                10000,
                600
        );

        Coordinate start2 = new Coordinate(48.8821, 9.1678);
        List<Coordinate> waypoints2 = Arrays.asList(start2, end);

        testRoute2 = new DrivingRoute(
                "route-2",
                "Test Route 2",
                "Test description 2",
                start2,
                end,
                waypoints2,
                12000,
                720
        );
    }

    @Nested
    @DisplayName("getAllRoutes Tests")
    class GetAllRoutesTests {

        @Test
        @DisplayName("Should return all routes from repository")
        void shouldReturnAllRoutes() {
            // Given
            List<DrivingRoute> expectedRoutes = Arrays.asList(testRoute1, testRoute2);
            when(routeRepository.findAll()).thenReturn(expectedRoutes);

            // When
            List<DrivingRoute> result = routeService.getAllRoutes();

            // Then
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("route-1", result.get(0).id());
            assertEquals("route-2", result.get(1).id());
            verify(routeRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no routes exist")
        void shouldReturnEmptyListWhenNoRoutes() {
            // Given
            when(routeRepository.findAll()).thenReturn(Collections.emptyList());

            // When
            List<DrivingRoute> result = routeService.getAllRoutes();

            // Then
            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(routeRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("getRouteById Tests")
    class GetRouteByIdTests {

        @Test
        @DisplayName("Should return route when found")
        void shouldReturnRouteWhenFound() {
            // Given
            when(routeRepository.findById("route-1")).thenReturn(Optional.of(testRoute1));

            // When
            DrivingRoute result = routeService.getRouteById("route-1");

            // Then
            assertNotNull(result);
            assertEquals("route-1", result.id());
            assertEquals("Test Route 1", result.name());
            verify(routeRepository, times(1)).findById("route-1");
        }

        @Test
        @DisplayName("Should throw RouteNotFoundException when route not found")
        void shouldThrowExceptionWhenNotFound() {
            // Given
            when(routeRepository.findById("non-existent")).thenReturn(Optional.empty());

            // When & Then
            assertThrows(RouteNotFoundException.class,
                    () -> routeService.getRouteById("non-existent"));
            verify(routeRepository, times(1)).findById("non-existent");
        }
    }

    @Nested
    @DisplayName("getRandomRoute Tests")
    class GetRandomRouteTests {

        @Test
        @DisplayName("Should return a random route from available routes")
        void shouldReturnRandomRoute() {
            // Given
            List<DrivingRoute> routes = Arrays.asList(testRoute1, testRoute2);
            when(routeRepository.findAll()).thenReturn(routes);

            // When
            DrivingRoute result = routeService.getRandomRoute();

            // Then
            assertNotNull(result);
            assertTrue(result.id().equals("route-1") || result.id().equals("route-2"));
            verify(routeRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Should throw RouteNotFoundException when no routes available")
        void shouldThrowExceptionWhenNoRoutesAvailable() {
            // Given
            when(routeRepository.findAll()).thenReturn(Collections.emptyList());

            // When & Then
            assertThrows(RouteNotFoundException.class,
                    () -> routeService.getRandomRoute());
        }

        @Test
        @DisplayName("Should return the only route when single route available")
        void shouldReturnOnlyRouteWhenSingleRouteAvailable() {
            // Given
            when(routeRepository.findAll()).thenReturn(Collections.singletonList(testRoute1));

            // When
            DrivingRoute result = routeService.getRandomRoute();

            // Then
            assertNotNull(result);
            assertEquals("route-1", result.id());
        }
    }

    @Nested
    @DisplayName("getRouteCount Tests")
    class GetRouteCountTests {

        @Test
        @DisplayName("Should return correct count of routes")
        void shouldReturnCorrectCount() {
            // Given
            when(routeRepository.count()).thenReturn(8);

            // When
            int result = routeService.getRouteCount();

            // Then
            assertEquals(8, result);
            verify(routeRepository, times(1)).count();
        }

        @Test
        @DisplayName("Should return zero when no routes")
        void shouldReturnZeroWhenNoRoutes() {
            // Given
            when(routeRepository.count()).thenReturn(0);

            // When
            int result = routeService.getRouteCount();

            // Then
            assertEquals(0, result);
        }
    }
}

