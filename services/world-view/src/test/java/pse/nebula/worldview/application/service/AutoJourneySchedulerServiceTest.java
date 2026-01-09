package pse.nebula.worldview.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pse.nebula.worldview.domain.model.Coordinate;
import pse.nebula.worldview.domain.model.DrivingRoute;
import pse.nebula.worldview.domain.model.JourneyState;
import pse.nebula.worldview.domain.model.JourneyStatus;
import pse.nebula.worldview.domain.port.inbound.JourneyUseCase;
import pse.nebula.worldview.domain.port.inbound.RouteUseCase;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AutoJourneySchedulerService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AutoJourneySchedulerService Tests")
class AutoJourneySchedulerServiceTest {

    @Mock
    private JourneyUseCase journeyUseCase;

    @Mock
    private RouteUseCase routeUseCase;

    private AutoJourneySchedulerService schedulerService;
    private DrivingRoute testRoute;

    @BeforeEach
    void setUp() {
        List<Coordinate> waypoints = Arrays.asList(
                new Coordinate(48.8973, 9.1920),
                new Coordinate(48.8800, 9.1750),
                new Coordinate(48.8354, 9.1520)
        );

        testRoute = new DrivingRoute(
                "test-route",
                "Test Route",
                "A test route",
                waypoints,
                10000,
                600
        );

        // Create with short delay for testing
        schedulerService = new AutoJourneySchedulerService(
                journeyUseCase,
                routeUseCase,
                500L,   // updateIntervalMs
                13.89,  // defaultSpeedMps
                100L    // delayBetweenJourneysMs (short for testing)
        );
    }

    @Nested
    @DisplayName("manageJourneys() Tests")
    class ManageJourneysTests {

        @Test
        @DisplayName("Should start new journey when no active journey")
        void shouldStartNewJourneyWhenNoActiveJourney() {
            // Given
            JourneyState newState = new JourneyState("auto-journey-test", testRoute, 13.89);
            newState.start();

            when(routeUseCase.getRandomRoute()).thenReturn(testRoute);
            when(journeyUseCase.startNewJourney(anyString(), eq(13.89))).thenReturn(newState);

            // When
            schedulerService.manageJourneys();

            // Then
            verify(routeUseCase).getRandomRoute();
            verify(journeyUseCase).startNewJourney(argThat(id -> id.startsWith("auto-journey-")), eq(13.89));
            assertTrue(schedulerService.hasActiveJourney());
        }

        @Test
        @DisplayName("Should advance active journey when in progress")
        void shouldAdvanceActiveJourneyWhenInProgress() {
            // Given - First start a journey
            JourneyState journeyState = new JourneyState("auto-journey-test", testRoute, 13.89);
            journeyState.start();

            when(routeUseCase.getRandomRoute()).thenReturn(testRoute);
            when(journeyUseCase.startNewJourney(anyString(), eq(13.89))).thenReturn(journeyState);

            schedulerService.manageJourneys(); // Start journey

            String journeyId = schedulerService.getActiveJourneyId().orElse("");

            when(journeyUseCase.journeyExists(journeyId)).thenReturn(true);
            when(journeyUseCase.getJourneyState(journeyId)).thenReturn(journeyState);
            when(journeyUseCase.advanceJourney(eq(journeyId), anyDouble())).thenReturn(journeyState.getCurrentPosition());

            // When
            schedulerService.manageJourneys();

            // Then
            verify(journeyUseCase).advanceJourney(eq(journeyId), anyDouble());
        }

        @Test
        @DisplayName("Should handle journey no longer exists")
        void shouldHandleJourneyNoLongerExists() {
            // Given - Start a journey first
            JourneyState journeyState = new JourneyState("auto-journey-test", testRoute, 13.89);
            journeyState.start();

            when(routeUseCase.getRandomRoute()).thenReturn(testRoute);
            when(journeyUseCase.startNewJourney(anyString(), eq(13.89))).thenReturn(journeyState);

            schedulerService.manageJourneys(); // Start journey

            String journeyId = schedulerService.getActiveJourneyId().orElse("");

            // Journey no longer exists
            when(journeyUseCase.journeyExists(journeyId)).thenReturn(false);

            // When
            schedulerService.manageJourneys();

            // Then
            assertFalse(schedulerService.hasActiveJourney());
        }

        @Test
        @DisplayName("Should handle completed journey")
        void shouldHandleCompletedJourney() {
            // Given - Start a journey first
            JourneyState journeyState = mock(JourneyState.class);
            when(journeyState.getStatus()).thenReturn(JourneyStatus.IN_PROGRESS);
            when(journeyState.getRoute()).thenReturn(testRoute);

            when(routeUseCase.getRandomRoute()).thenReturn(testRoute);
            when(journeyUseCase.startNewJourney(anyString(), eq(13.89))).thenReturn(journeyState);

            schedulerService.manageJourneys(); // Start journey

            String journeyId = schedulerService.getActiveJourneyId().orElse("");

            // Journey is now completed
            when(journeyUseCase.journeyExists(journeyId)).thenReturn(true);
            when(journeyState.getStatus()).thenReturn(JourneyStatus.COMPLETED);
            when(journeyUseCase.getJourneyState(journeyId)).thenReturn(journeyState);

            // When
            schedulerService.manageJourneys();

            // Then
            verify(journeyUseCase).stopJourney(journeyId);
            assertFalse(schedulerService.hasActiveJourney());
        }

        @Test
        @DisplayName("Should handle exception during journey processing")
        void shouldHandleExceptionDuringJourneyProcessing() {
            // Given - Start a journey first
            JourneyState journeyState = new JourneyState("auto-journey-test", testRoute, 13.89);
            journeyState.start();

            when(routeUseCase.getRandomRoute()).thenReturn(testRoute);
            when(journeyUseCase.startNewJourney(anyString(), eq(13.89))).thenReturn(journeyState);

            schedulerService.manageJourneys(); // Start journey

            String journeyId = schedulerService.getActiveJourneyId().orElse("");

            // Throw exception when checking journey
            when(journeyUseCase.journeyExists(journeyId)).thenThrow(new RuntimeException("DB error"));

            // When
            schedulerService.manageJourneys();

            // Then - Should clear active journey on error
            assertFalse(schedulerService.hasActiveJourney());
        }

        @Test
        @DisplayName("Should handle failed journey start")
        void shouldHandleFailedJourneyStart() {
            // Given
            when(routeUseCase.getRandomRoute()).thenThrow(new RuntimeException("No routes available"));

            // When
            schedulerService.manageJourneys();

            // Then - Should not crash and no active journey
            assertFalse(schedulerService.hasActiveJourney());
        }
    }

    @Nested
    @DisplayName("getActiveJourneyId() Tests")
    class GetActiveJourneyIdTests {

        @Test
        @DisplayName("Should return empty when no active journey")
        void shouldReturnEmptyWhenNoActiveJourney() {
            Optional<String> result = schedulerService.getActiveJourneyId();
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should return journey ID when active")
        void shouldReturnJourneyIdWhenActive() {
            // Given
            JourneyState journeyState = new JourneyState("auto-journey-test", testRoute, 13.89);
            journeyState.start();

            when(routeUseCase.getRandomRoute()).thenReturn(testRoute);
            when(journeyUseCase.startNewJourney(anyString(), eq(13.89))).thenReturn(journeyState);

            schedulerService.manageJourneys();

            // When
            Optional<String> result = schedulerService.getActiveJourneyId();

            // Then
            assertTrue(result.isPresent());
            assertTrue(result.get().startsWith("auto-journey-"));
        }
    }

    @Nested
    @DisplayName("hasActiveJourney() Tests")
    class HasActiveJourneyTests {

        @Test
        @DisplayName("Should return false when no active journey")
        void shouldReturnFalseWhenNoActiveJourney() {
            assertFalse(schedulerService.hasActiveJourney());
        }

        @Test
        @DisplayName("Should return true when journey is active")
        void shouldReturnTrueWhenJourneyIsActive() {
            // Given
            JourneyState journeyState = new JourneyState("auto-journey-test", testRoute, 13.89);
            journeyState.start();

            when(routeUseCase.getRandomRoute()).thenReturn(testRoute);
            when(journeyUseCase.startNewJourney(anyString(), eq(13.89))).thenReturn(journeyState);

            schedulerService.manageJourneys();

            // Then
            assertTrue(schedulerService.hasActiveJourney());
        }
    }

    @Nested
    @DisplayName("getActiveJourneyState() Tests")
    class GetActiveJourneyStateTests {

        @Test
        @DisplayName("Should return empty when no active journey")
        void shouldReturnEmptyWhenNoActiveJourney() {
            Optional<JourneyState> result = schedulerService.getActiveJourneyState();
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should return journey state when active")
        void shouldReturnJourneyStateWhenActive() {
            // Given
            JourneyState journeyState = new JourneyState("auto-journey-test", testRoute, 13.89);
            journeyState.start();

            when(routeUseCase.getRandomRoute()).thenReturn(testRoute);
            when(journeyUseCase.startNewJourney(anyString(), eq(13.89))).thenReturn(journeyState);

            schedulerService.manageJourneys();

            String journeyId = schedulerService.getActiveJourneyId().orElse("");
            when(journeyUseCase.getJourneyState(journeyId)).thenReturn(journeyState);

            // When
            Optional<JourneyState> result = schedulerService.getActiveJourneyState();

            // Then
            assertTrue(result.isPresent());
        }

        @Test
        @DisplayName("Should return empty when getJourneyState throws exception")
        void shouldReturnEmptyWhenGetJourneyStateThrowsException() {
            // Given
            JourneyState journeyState = new JourneyState("auto-journey-test", testRoute, 13.89);
            journeyState.start();

            when(routeUseCase.getRandomRoute()).thenReturn(testRoute);
            when(journeyUseCase.startNewJourney(anyString(), eq(13.89))).thenReturn(journeyState);

            schedulerService.manageJourneys();

            String journeyId = schedulerService.getActiveJourneyId().orElse("");
            when(journeyUseCase.getJourneyState(journeyId)).thenThrow(new RuntimeException("Not found"));

            // When
            Optional<JourneyState> result = schedulerService.getActiveJourneyState();

            // Then
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Delay Between Journeys Tests")
    class DelayBetweenJourneysTests {

        @Test
        @DisplayName("Should wait before starting new journey after completion")
        void shouldWaitBeforeStartingNewJourneyAfterCompletion() {
            // Given - Create scheduler with longer delay
            AutoJourneySchedulerService longDelayScheduler = new AutoJourneySchedulerService(
                    journeyUseCase, routeUseCase, 500L, 13.89, 1000L // 1-second delay
            );

            JourneyState journeyState = mock(JourneyState.class);
            when(journeyState.getStatus()).thenReturn(JourneyStatus.IN_PROGRESS);
            when(journeyState.getRoute()).thenReturn(testRoute);

            when(routeUseCase.getRandomRoute()).thenReturn(testRoute);
            when(journeyUseCase.startNewJourney(anyString(), eq(13.89))).thenReturn(journeyState);

            // Start first journey
            longDelayScheduler.manageJourneys();
            String journeyId = longDelayScheduler.getActiveJourneyId().orElse("");

            // Complete the journey
            when(journeyUseCase.journeyExists(journeyId)).thenReturn(true);
            when(journeyState.getStatus()).thenReturn(JourneyStatus.COMPLETED);
            when(journeyUseCase.getJourneyState(journeyId)).thenReturn(journeyState);

            longDelayScheduler.manageJourneys(); // This should complete the journey

            // Reset mocks for new journey attempt
            reset(journeyUseCase, routeUseCase);

            // When - Try to start new journey immediately
            longDelayScheduler.manageJourneys();

            // Then - Should not start new journey yet (delay not elapsed)
            verify(routeUseCase, never()).getRandomRoute();
        }
    }

    @Nested
    @DisplayName("Journey Completion Tests")
    class JourneyCompletionTests {

        @Test
        @DisplayName("Should handle stopJourney exception during completion")
        void shouldHandleStopJourneyExceptionDuringCompletion() {
            // Given
            JourneyState journeyState = mock(JourneyState.class);
            when(journeyState.getStatus()).thenReturn(JourneyStatus.IN_PROGRESS);
            when(journeyState.getRoute()).thenReturn(testRoute);

            when(routeUseCase.getRandomRoute()).thenReturn(testRoute);
            when(journeyUseCase.startNewJourney(anyString(), eq(13.89))).thenReturn(journeyState);

            schedulerService.manageJourneys(); // Start journey
            String journeyId = schedulerService.getActiveJourneyId().orElse("");

            // Journey completes but stopJourney throws
            when(journeyUseCase.journeyExists(journeyId)).thenReturn(true);
            when(journeyState.getStatus()).thenReturn(JourneyStatus.COMPLETED);
            when(journeyUseCase.getJourneyState(journeyId)).thenReturn(journeyState);
            doThrow(new RuntimeException("Already deleted")).when(journeyUseCase).stopJourney(journeyId);

            // When
            schedulerService.manageJourneys();

            // Then - Should still clear active journey
            assertFalse(schedulerService.hasActiveJourney());
        }

        @Test
        @DisplayName("Should detect completion after advance")
        void shouldDetectCompletionAfterAdvance() {
            // Given
            JourneyState journeyState = mock(JourneyState.class);
            when(journeyState.getStatus())
                    .thenReturn(JourneyStatus.IN_PROGRESS)  // First call (start check)
                    .thenReturn(JourneyStatus.IN_PROGRESS)  // Second call (during active journey handling)
                    .thenReturn(JourneyStatus.COMPLETED);    // Third call (after advance)
            when(journeyState.getRoute()).thenReturn(testRoute);

            when(routeUseCase.getRandomRoute()).thenReturn(testRoute);
            when(journeyUseCase.startNewJourney(anyString(), eq(13.89))).thenReturn(journeyState);

            schedulerService.manageJourneys(); // Start journey
            String journeyId = schedulerService.getActiveJourneyId().orElse("");

            when(journeyUseCase.journeyExists(journeyId)).thenReturn(true);
            when(journeyUseCase.getJourneyState(journeyId)).thenReturn(journeyState);
            when(journeyUseCase.advanceJourney(eq(journeyId), anyDouble())).thenReturn(testRoute.endPoint());

            // When
            schedulerService.manageJourneys();

            // Then
            verify(journeyUseCase).stopJourney(journeyId);
        }
    }
}