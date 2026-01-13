package pse.nebula.worldview.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pse.nebula.worldview.domain.exception.JourneyAlreadyExistsException;
import pse.nebula.worldview.domain.exception.JourneyNotFoundException;
import pse.nebula.worldview.domain.model.Coordinate;
import pse.nebula.worldview.domain.model.DrivingRoute;
import pse.nebula.worldview.domain.model.JourneyState;
import pse.nebula.worldview.domain.model.JourneyStatus;
import pse.nebula.worldview.domain.port.inbound.RouteUseCase;
import pse.nebula.worldview.domain.port.outbound.CoordinatePublisher;
import pse.nebula.worldview.domain.port.outbound.JourneyStateRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for JourneyService.
 * Tests the simplified auto-managed journey service.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("JourneyService Unit Tests")
class JourneyServiceTest {

    @Mock
    private RouteUseCase routeUseCase;

    @Mock
    private JourneyStateRepository journeyStateRepository;

    @Mock
    private CoordinatePublisher coordinatePublisher;

    @InjectMocks
    private JourneyService journeyService;

    private DrivingRoute testRoute;
    private static final String JOURNEY_ID = "test-journey-1";
    private static final double DEFAULT_SPEED = 13.89; // ~50 km/h

    @BeforeEach
    void setUp() {
        Coordinate start = new Coordinate(48.8973, 9.1920);
        Coordinate end = new Coordinate(48.8354, 9.1520);
        List<Coordinate> waypoints = Arrays.asList(
                start,
                new Coordinate(48.8800, 9.1750),
                new Coordinate(48.8600, 9.1600),
                end
        );

        testRoute = new DrivingRoute(
                "route-1",
                "Test Route",
                "Test description",
                waypoints,
                10000,
                600
        );
    }

    @Nested
    @DisplayName("startNewJourney Tests")
    class StartNewJourneyTests {

        @Test
        @DisplayName("Should create and start a new journey on random route")
        void shouldCreateAndStartNewJourney() {
            // Given
            when(routeUseCase.getRandomRoute()).thenReturn(testRoute);
            when(journeyStateRepository.exists(JOURNEY_ID)).thenReturn(false);

            // When
            JourneyState result = journeyService.startNewJourney(JOURNEY_ID, DEFAULT_SPEED);

            // Then
            assertNotNull(result);
            assertEquals(JOURNEY_ID, result.getJourneyId());
            assertEquals(JourneyStatus.IN_PROGRESS, result.getStatus());
            assertEquals(DEFAULT_SPEED, result.getSpeedMetersPerSecond());
            assertEquals(testRoute, result.getRoute());

            verify(journeyStateRepository).save(any(JourneyState.class));
            verify(coordinatePublisher).publishJourneyStarted(any(JourneyState.class));
        }

        @Test
        @DisplayName("Should throw exception when journey already exists")
        void shouldThrowExceptionWhenJourneyExists() {
            // Given
            when(routeUseCase.getRandomRoute()).thenReturn(testRoute);
            when(journeyStateRepository.exists(JOURNEY_ID)).thenReturn(true);

            // When & Then
            assertThrows(JourneyAlreadyExistsException.class,
                    () -> journeyService.startNewJourney(JOURNEY_ID, DEFAULT_SPEED));

            verify(journeyStateRepository, never()).save(any());
            verify(coordinatePublisher, never()).publishJourneyStarted(any());
        }

        @Test
        @DisplayName("Should set initial position at route start")
        void shouldSetInitialPositionAtRouteStart() {
            // Given
            when(routeUseCase.getRandomRoute()).thenReturn(testRoute);
            when(journeyStateRepository.exists(JOURNEY_ID)).thenReturn(false);

            // When
            JourneyState result = journeyService.startNewJourney(JOURNEY_ID, DEFAULT_SPEED);

            // Then
            assertEquals(testRoute.startPoint(), result.getCurrentPosition());
            assertEquals(0, result.getCurrentWaypointIndex());
            assertEquals(0.0, result.getProgressPercentage(), 0.01);
        }
    }

    @Nested
    @DisplayName("getJourneyState Tests")
    class GetJourneyStateTests {

        @Test
        @DisplayName("Should return journey state when found")
        void shouldReturnJourneyStateWhenFound() {
            // Given
            JourneyState expectedState = new JourneyState(JOURNEY_ID, testRoute, DEFAULT_SPEED);
            when(journeyStateRepository.findById(JOURNEY_ID)).thenReturn(Optional.of(expectedState));

            // When
            JourneyState result = journeyService.getJourneyState(JOURNEY_ID);

            // Then
            assertNotNull(result);
            assertEquals(JOURNEY_ID, result.getJourneyId());
        }

        @Test
        @DisplayName("Should throw JourneyNotFoundException when not found")
        void shouldThrowExceptionWhenNotFound() {
            // Given
            when(journeyStateRepository.findById(JOURNEY_ID)).thenReturn(Optional.empty());

            // When & Then
            assertThrows(JourneyNotFoundException.class,
                    () -> journeyService.getJourneyState(JOURNEY_ID));
        }
    }

    @Nested
    @DisplayName("advanceJourney Tests")
    class AdvanceJourneyTests {

        @Test
        @DisplayName("Should advance journey and return new position")
        void shouldAdvanceJourneyAndReturnPosition() {
            // Given
            JourneyState journeyState = new JourneyState(JOURNEY_ID, testRoute, DEFAULT_SPEED);
            journeyState.start();
            when(journeyStateRepository.findById(JOURNEY_ID)).thenReturn(Optional.of(journeyState));

            // When
            Coordinate result = journeyService.advanceJourney(JOURNEY_ID, 2.0);

            // Then
            assertNotNull(result);
            verify(journeyStateRepository).save(journeyState);
            verify(coordinatePublisher).publishCoordinateUpdate(eq(JOURNEY_ID), any(Coordinate.class), eq(journeyState));
        }

        @Test
        @DisplayName("Should publish journey completed when destination reached")
        void shouldPublishCompletedWhenDestinationReached() {
            // Given - Create a very short route that will complete quickly
            Coordinate start = new Coordinate(48.8354, 9.1520);
            Coordinate end = new Coordinate(48.8354, 9.1521); // Very close
            List<Coordinate> shortWaypoints = Arrays.asList(start, end);
            DrivingRoute shortRoute = new DrivingRoute(
                    "short-route", "Short", "Short route",
                    shortWaypoints, 10, 1
            );

            JourneyState journeyState = new JourneyState(JOURNEY_ID, shortRoute, 100.0); // High speed
            journeyState.start();
            when(journeyStateRepository.findById(JOURNEY_ID)).thenReturn(Optional.of(journeyState));

            // When - Advance enough to complete the journey
            journeyService.advanceJourney(JOURNEY_ID, 10.0);

            // Then
            verify(coordinatePublisher).publishJourneyCompleted(journeyState);
        }

        @Test
        @DisplayName("Should update progress percentage during advancement")
        void shouldUpdateProgressDuringAdvancement() {
            // Given
            JourneyState journeyState = new JourneyState(JOURNEY_ID, testRoute, 1000.0); // Fast speed
            journeyState.start();
            when(journeyStateRepository.findById(JOURNEY_ID)).thenReturn(Optional.of(journeyState));

            // When
            journeyService.advanceJourney(JOURNEY_ID, 1.0);

            // Then
            assertTrue(journeyState.getProgressPercentage() > 0);
        }
    }

    @Nested
    @DisplayName("stopJourney Tests")
    class StopJourneyTests {

        @Test
        @DisplayName("Should stop and delete journey")
        void shouldStopAndDeleteJourney() {
            // When
            journeyService.stopJourney(JOURNEY_ID);

            // Then
            verify(journeyStateRepository).delete(JOURNEY_ID);
        }
    }

    @Nested
    @DisplayName("journeyExists Tests")
    class JourneyExistsTests {

        @Test
        @DisplayName("Should return true when journey exists")
        void shouldReturnTrueWhenJourneyExists() {
            // Given
            when(journeyStateRepository.exists(JOURNEY_ID)).thenReturn(true);

            // When
            boolean result = journeyService.journeyExists(JOURNEY_ID);

            // Then
            assertTrue(result);
        }

        @Test
        @DisplayName("Should return false when journey does not exist")
        void shouldReturnFalseWhenJourneyDoesNotExist() {
            // Given
            when(journeyStateRepository.exists(JOURNEY_ID)).thenReturn(false);

            // When
            boolean result = journeyService.journeyExists(JOURNEY_ID);

            // Then
            assertFalse(result);
        }
    }
}

