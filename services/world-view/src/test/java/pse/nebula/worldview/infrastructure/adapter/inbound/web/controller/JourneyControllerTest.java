package pse.nebula.worldview.infrastructure.adapter.inbound.web.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pse.nebula.worldview.application.service.AutoJourneySchedulerService;
import pse.nebula.worldview.domain.exception.JourneyNotFoundException;
import pse.nebula.worldview.domain.model.Coordinate;
import pse.nebula.worldview.domain.model.DrivingRoute;
import pse.nebula.worldview.domain.model.JourneyState;
import pse.nebula.worldview.domain.port.inbound.JourneyUseCase;
import pse.nebula.worldview.infrastructure.adapter.inbound.web.dto.JourneyStateDto;
import pse.nebula.worldview.infrastructure.adapter.inbound.web.mapper.DtoMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for JourneyController.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("JourneyController Tests")
class JourneyControllerTest {

    @Mock
    private JourneyUseCase journeyUseCase;

    @Mock
    private AutoJourneySchedulerService autoJourneySchedulerService;

    @Mock
    private DtoMapper dtoMapper;

    @InjectMocks
    private JourneyController journeyController;

    private DrivingRoute testRoute;
    private JourneyState testJourneyState;
    private JourneyStateDto testJourneyStateDto;

    @BeforeEach
    void setUp() {
        List<Coordinate> waypoints = Arrays.asList(
                new Coordinate(48.8973, 9.1920),
                new Coordinate(48.8354, 9.1520)
        );

        testRoute = new DrivingRoute(
                "route-1",
                "Test Route",
                "Test description",
                waypoints,
                5000.0,
                600
        );

        testJourneyState = new JourneyState("journey-1", testRoute, 13.89);
        testJourneyState.start();

        testJourneyStateDto = JourneyStateDto.builder()
                .journeyId("journey-1")
                .status("IN_PROGRESS")
                .build();
    }

    @Nested
    @DisplayName("GET /api/v1/journeys/current Tests")
    class GetCurrentJourneyTests {

        @Test
        @DisplayName("Should return current journey when active")
        void shouldReturnCurrentJourneyWhenActive() {
            // Given
            when(autoJourneySchedulerService.getActiveJourneyState())
                    .thenReturn(Optional.of(testJourneyState));
            when(dtoMapper.toDto(testJourneyState)).thenReturn(testJourneyStateDto);

            // When
            ResponseEntity<JourneyStateDto> response = journeyController.getCurrentJourney();

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("journey-1", response.getBody().getJourneyId());
            verify(autoJourneySchedulerService).getActiveJourneyState();
            verify(dtoMapper).toDto(testJourneyState);
        }

        @Test
        @DisplayName("Should return 204 No Content when no active journey")
        void shouldReturn204WhenNoActiveJourney() {
            // Given
            when(autoJourneySchedulerService.getActiveJourneyState())
                    .thenReturn(Optional.empty());

            // When
            ResponseEntity<JourneyStateDto> response = journeyController.getCurrentJourney();

            // Then
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            assertNull(response.getBody());
            verify(autoJourneySchedulerService).getActiveJourneyState();
            verify(dtoMapper, never()).toDto(any(JourneyState.class));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/journeys/active Tests")
    class IsJourneyActiveTests {

        @Test
        @DisplayName("Should return true when journey is active")
        void shouldReturnTrueWhenJourneyIsActive() {
            // Given
            when(autoJourneySchedulerService.hasActiveJourney()).thenReturn(true);

            // When
            ResponseEntity<Boolean> response = journeyController.isJourneyActive();

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(response.getBody());
            verify(autoJourneySchedulerService).hasActiveJourney();
        }

        @Test
        @DisplayName("Should return false when no journey is active")
        void shouldReturnFalseWhenNoJourneyIsActive() {
            // Given
            when(autoJourneySchedulerService.hasActiveJourney()).thenReturn(false);

            // When
            ResponseEntity<Boolean> response = journeyController.isJourneyActive();

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertFalse(response.getBody());
            verify(autoJourneySchedulerService).hasActiveJourney();
        }
    }

    @Nested
    @DisplayName("GET /api/v1/journeys/{journeyId} Tests")
    class GetJourneyStateTests {

        @Test
        @DisplayName("Should return journey state when found")
        void shouldReturnJourneyStateWhenFound() {
            // Given
            when(journeyUseCase.getJourneyState("journey-1")).thenReturn(testJourneyState);
            when(dtoMapper.toDto(testJourneyState)).thenReturn(testJourneyStateDto);

            // When
            ResponseEntity<JourneyStateDto> response = journeyController.getJourneyState("journey-1");

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("journey-1", response.getBody().getJourneyId());
            verify(journeyUseCase).getJourneyState("journey-1");
            verify(dtoMapper).toDto(testJourneyState);
        }

        @Test
        @DisplayName("Should propagate JourneyNotFoundException")
        void shouldPropagateJourneyNotFoundException() {
            // Given
            when(journeyUseCase.getJourneyState("non-existent"))
                    .thenThrow(new JourneyNotFoundException("non-existent"));

            // When & Then
            assertThrows(JourneyNotFoundException.class, () ->
                    journeyController.getJourneyState("non-existent"));
            verify(journeyUseCase).getJourneyState("non-existent");
        }
    }
}