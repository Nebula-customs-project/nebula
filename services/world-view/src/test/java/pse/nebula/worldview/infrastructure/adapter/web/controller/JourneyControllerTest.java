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
import pse.nebula.worldview.application.service.AutoJourneySchedulerService;
import pse.nebula.worldview.domain.exception.JourneyNotFoundException;
import pse.nebula.worldview.domain.model.Coordinate;
import pse.nebula.worldview.domain.model.DrivingRoute;
import pse.nebula.worldview.domain.model.JourneyState;
import pse.nebula.worldview.domain.model.JourneyStatus;
import pse.nebula.worldview.domain.port.inbound.JourneyUseCase;
import pse.nebula.worldview.infrastructure.adapter.inbound.web.controller.JourneyController;
import pse.nebula.worldview.infrastructure.adapter.inbound.web.dto.JourneyStateDto;
import pse.nebula.worldview.infrastructure.adapter.inbound.web.mapper.DtoMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for JourneyController.
 * Tests the read-only journey endpoints for auto-managed journeys.
 */
@WebMvcTest(JourneyController.class)
@DisplayName("JourneyController Unit Tests")
class JourneyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JourneyUseCase journeyUseCase;

    @MockitoBean
    private AutoJourneySchedulerService autoJourneySchedulerService;

    @MockitoBean
    private DtoMapper dtoMapper;

    private DrivingRoute testRoute;
    private JourneyState testJourneyState;

    @BeforeEach
    void setUp() {
        Coordinate start = new Coordinate(48.8973, 9.1920);
        Coordinate end = new Coordinate(48.8354, 9.1520);
        List<Coordinate> waypoints = Arrays.asList(start, end);

        testRoute = new DrivingRoute(
                "route-1",
                "Test Route",
                "Test description",
                waypoints,
                10000,
                600
        );

        testJourneyState = new JourneyState("auto-journey-12345", testRoute, 13.89);
        testJourneyState.start();

        // Setup default mapper behavior
        when(dtoMapper.toDto(any(JourneyState.class))).thenAnswer(invocation -> {
            JourneyState state = invocation.getArgument(0);
            return JourneyStateDto.builder()
                    .journeyId(state.getJourneyId())
                    .status(state.getStatus().name())
                    .progressPercentage(state.getProgressPercentage())
                    .build();
        });
    }

    @Nested
    @DisplayName("GET /api/v1/journeys/current Tests")
    class GetCurrentJourneyTests {

        @Test
        @DisplayName("Should return current journey when active")
        void shouldReturnCurrentJourneyWhenActive() throws Exception {
            // Given
            when(autoJourneySchedulerService.getActiveJourneyState())
                    .thenReturn(Optional.of(testJourneyState));

            // When & Then
            mockMvc.perform(get("/api/v1/journeys/current"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.journey_id", is("auto-journey-12345")))
                    .andExpect(jsonPath("$.status", is("IN_PROGRESS")));

            verify(autoJourneySchedulerService, times(1)).getActiveJourneyState();
        }

        @Test
        @DisplayName("Should return 204 No Content when no active journey")
        void shouldReturn204WhenNoActiveJourney() throws Exception {
            // Given
            when(autoJourneySchedulerService.getActiveJourneyState())
                    .thenReturn(Optional.empty());

            // When & Then
            mockMvc.perform(get("/api/v1/journeys/current"))
                    .andExpect(status().isNoContent());

            verify(autoJourneySchedulerService, times(1)).getActiveJourneyState();
        }
    }

    @Nested
    @DisplayName("GET /api/v1/journeys/active Tests")
    class IsJourneyActiveTests {

        @Test
        @DisplayName("Should return true when journey is active")
        void shouldReturnTrueWhenActive() throws Exception {
            // Given
            when(autoJourneySchedulerService.hasActiveJourney()).thenReturn(true);

            // When & Then
            mockMvc.perform(get("/api/v1/journeys/active"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));

            verify(autoJourneySchedulerService, times(1)).hasActiveJourney();
        }

        @Test
        @DisplayName("Should return false when no active journey")
        void shouldReturnFalseWhenNotActive() throws Exception {
            // Given
            when(autoJourneySchedulerService.hasActiveJourney()).thenReturn(false);

            // When & Then
            mockMvc.perform(get("/api/v1/journeys/active"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("false"));

            verify(autoJourneySchedulerService, times(1)).hasActiveJourney();
        }
    }

    @Nested
    @DisplayName("GET /api/v1/journeys/{journeyId} Tests")
    class GetJourneyByIdTests {

        @Test
        @DisplayName("Should return journey state when found")
        void shouldReturnJourneyStateWhenFound() throws Exception {
            // Given
            when(journeyUseCase.getJourneyState("auto-journey-12345"))
                    .thenReturn(testJourneyState);

            // When & Then
            mockMvc.perform(get("/api/v1/journeys/auto-journey-12345"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.journey_id", is("auto-journey-12345")));

            verify(journeyUseCase, times(1)).getJourneyState("auto-journey-12345");
        }

        @Test
        @DisplayName("Should return 404 when journey not found")
        void shouldReturn404WhenNotFound() throws Exception {
            // Given
            when(journeyUseCase.getJourneyState("non-existent"))
                    .thenThrow(new JourneyNotFoundException("non-existent"));

            // When & Then
            mockMvc.perform(get("/api/v1/journeys/non-existent"))
                    .andExpect(status().isNotFound());

            verify(journeyUseCase, times(1)).getJourneyState("non-existent");
        }
    }

    @Nested
    @DisplayName("Removed Endpoints Tests")
    class RemovedEndpointsTests {

        @Test
        @DisplayName("POST /api/v1/journeys should not exist (start journey)")
        void postJourneysShouldNotExist() throws Exception {
            mockMvc.perform(post("/api/v1/journeys")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isMethodNotAllowed());
        }

        @Test
        @DisplayName("POST /api/v1/journeys/{id}/pause should not exist")
        void pauseEndpointShouldNotExist() throws Exception {
            mockMvc.perform(post("/api/v1/journeys/test-id/pause"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("POST /api/v1/journeys/{id}/resume should not exist")
        void resumeEndpointShouldNotExist() throws Exception {
            mockMvc.perform(post("/api/v1/journeys/test-id/resume"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("DELETE /api/v1/journeys/{id} should not exist (stop journey)")
        void deleteJourneyShouldNotExist() throws Exception {
            mockMvc.perform(delete("/api/v1/journeys/test-id"))
                    .andExpect(status().isMethodNotAllowed());
        }
    }
}

