package pse.nebula.worldview.infrastructure.adapter.web.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pse.nebula.worldview.domain.exception.RouteNotFoundException;
import pse.nebula.worldview.domain.model.Coordinate;
import pse.nebula.worldview.domain.model.DrivingRoute;
import pse.nebula.worldview.domain.port.inbound.RouteUseCase;
import pse.nebula.worldview.infrastructure.adapter.web.mapper.DtoMapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RouteController.class)
@DisplayName("RouteController Unit Tests")
class RouteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RouteUseCase routeUseCase;

    @MockitoBean
    private DtoMapper dtoMapper;

    private DrivingRoute testRoute1;
    private DrivingRoute testRoute2;

    @BeforeEach
    void setUp() {
        Coordinate start1 = new Coordinate(48.8973, 9.1920);
        Coordinate end = new Coordinate(48.8354, 9.1520);
        List<Coordinate> waypoints1 = Arrays.asList(start1, end);

        testRoute1 = new DrivingRoute(
                "route-1",
                "Ludwigsburg Schloss Route",
                "From Ludwigsburg Palace via B27",
                waypoints1,
                15000,
                1200
        );

        Coordinate start2 = new Coordinate(48.8821, 9.1678);
        List<Coordinate> waypoints2 = Arrays.asList(start2, end);

        testRoute2 = new DrivingRoute(
                "route-2",
                "Favoritepark Route",
                "From Favoritepark Ludwigsburg",
                waypoints2,
                12000,
                1080
        );

        // Setup mapper mock behavior
        when(dtoMapper.toDto(org.mockito.ArgumentMatchers.any(DrivingRoute.class))).thenAnswer(invocation -> {
            DrivingRoute route = invocation.getArgument(0);
            return pse.nebula.worldview.infrastructure.adapter.web.dto.RouteDto.builder()
                    .id(route.id())
                    .name(route.name())
                    .description(route.description())
                    .totalDistanceMeters(route.totalDistanceMeters())
                    .estimatedDurationSeconds(route.estimatedDurationSeconds())
                    .build();
        });
    }

    @Nested
    @DisplayName("GET /api/v1/routes Tests")
    class GetAllRoutesTests {

        @Test
        @DisplayName("Should return all routes with 200 OK")
        void shouldReturnAllRoutes() throws Exception {
            // Given
            List<DrivingRoute> routes = Arrays.asList(testRoute1, testRoute2);
            when(routeUseCase.getAllRoutes()).thenReturn(routes);

            // When & Then
            mockMvc.perform(get("/api/v1/routes"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is("route-1")))
                    .andExpect(jsonPath("$[1].id", is("route-2")));

            verify(routeUseCase, times(1)).getAllRoutes();
        }

        @Test
        @DisplayName("Should return empty list when no routes")
        void shouldReturnEmptyList() throws Exception {
            // Given
            when(routeUseCase.getAllRoutes()).thenReturn(Collections.emptyList());

            // When & Then
            mockMvc.perform(get("/api/v1/routes"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/routes/{routeId} Tests")
    class GetRouteByIdTests {

        @Test
        @DisplayName("Should return route when found")
        void shouldReturnRouteWhenFound() throws Exception {
            // Given
            when(routeUseCase.getRouteById("route-1")).thenReturn(testRoute1);

            // When & Then
            mockMvc.perform(get("/api/v1/routes/route-1"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is("route-1")))
                    .andExpect(jsonPath("$.name", is("Ludwigsburg Schloss Route")));

            verify(routeUseCase, times(1)).getRouteById("route-1");
        }

        @Test
        @DisplayName("Should return 404 when route not found")
        void shouldReturn404WhenNotFound() throws Exception {
            // Given
            when(routeUseCase.getRouteById("non-existent"))
                    .thenThrow(new RouteNotFoundException("non-existent"));

            // When & Then
            mockMvc.perform(get("/api/v1/routes/non-existent"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/routes/random Tests")
    class GetRandomRouteTests {

        @Test
        @DisplayName("Should return a random route")
        void shouldReturnRandomRoute() throws Exception {
            // Given
            when(routeUseCase.getRandomRoute()).thenReturn(testRoute1);

            // When & Then
            mockMvc.perform(get("/api/v1/routes/random"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", notNullValue()));

            verify(routeUseCase, times(1)).getRandomRoute();
        }
    }

    @Nested
    @DisplayName("GET /api/v1/routes/count Tests")
    class GetRouteCountTests {

        @Test
        @DisplayName("Should return route count")
        void shouldReturnRouteCount() throws Exception {
            // Given
            when(routeUseCase.getRouteCount()).thenReturn(8);

            // When & Then
            mockMvc.perform(get("/api/v1/routes/count"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("8"));

            verify(routeUseCase, times(1)).getRouteCount();
        }
    }
}

