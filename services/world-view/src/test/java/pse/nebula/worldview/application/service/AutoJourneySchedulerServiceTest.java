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
 * Tests the automatic journey management functionality.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AutoJourneySchedulerService Unit Tests")
class AutoJourneySchedulerServiceTest {

    @Mock
    private JourneyUseCase journeyUseCase;

    @Mock
    private RouteUseCase routeUseCase;

    private AutoJourneySchedulerService autoJourneySchedulerService;

    private DrivingRoute testRoute;

    private static final long UPDATE_INTERVAL_MS = 500;
    private static final double DEFAULT_SPEED_MPS = 13.89;
    private static final long DELAY_BETWEEN_JOURNEYS_MS = 1000;

    @BeforeEach
    void setUp() {
        autoJourneySchedulerService = new AutoJourneySchedulerService(
                journeyUseCase,
                routeUseCase,
                UPDATE_INTERVAL_MS,
                DEFAULT_SPEED_MPS,
                DELAY_BETWEEN_JOURNEYS_MS
        );

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
    @DisplayName("Initial State Tests")
    class InitialStateTests {

        @Test
        @DisplayName("Should have no active journey initially")
        void shouldHaveNoActiveJourneyInitially() {
            assertFalse(autoJourneySchedulerService.hasActiveJourney());
            assertTrue(autoJourneySchedulerService.getActiveJourneyId().isEmpty());
            assertTrue(autoJourneySchedulerService.getActiveJourneyState().isEmpty());
        }
    }

    @Nested
    @DisplayName("manageJourneys Tests - Starting New Journey")
    class ManageJourneysStartTests {

        @Test
        @DisplayName("Should start a new journey when no active journey exists")
        void shouldStartNewJourneyWhenNoActiveJourney() {
            // Given
            when(routeUseCase.getRandomRoute()).thenReturn(testRoute);
            JourneyState mockJourneyState = new JourneyState("test-journey", testRoute, DEFAULT_SPEED_MPS);
            mockJourneyState.start();
            when(journeyUseCase.startNewJourney(anyString(), eq(DEFAULT_SPEED_MPS))).thenReturn(mockJourneyState);

            // When
            autoJourneySchedulerService.manageJourneys();

            // Then
            verify(journeyUseCase).startNewJourney(argThat(id -> id.startsWith("auto-journey-")), eq(DEFAULT_SPEED_MPS));
            assertTrue(autoJourneySchedulerService.hasActiveJourney());
        }

        @Test
        @DisplayName("Should not start journey if one already exists")
        void shouldNotStartJourneyIfOneExists() {
            // Given - First start a journey
            when(routeUseCase.getRandomRoute()).thenReturn(testRoute);
            JourneyState mockJourneyState = new JourneyState("test-journey", testRoute, DEFAULT_SPEED_MPS);
            mockJourneyState.start();
            when(journeyUseCase.startNewJourney(anyString(), eq(DEFAULT_SPEED_MPS))).thenReturn(mockJourneyState);
            when(journeyUseCase.journeyExists(anyString())).thenReturn(true);
            when(journeyUseCase.getJourneyState(anyString())).thenReturn(mockJourneyState);

            autoJourneySchedulerService.manageJourneys();
            reset(journeyUseCase);

            // Setup for second call
            when(journeyUseCase.journeyExists(anyString())).thenReturn(true);
            when(journeyUseCase.getJourneyState(anyString())).thenReturn(mockJourneyState);

            // When - Run scheduler again
            autoJourneySchedulerService.manageJourneys();

            // Then - Should not start a new journey
            verify(journeyUseCase, never()).startNewJourney(anyString(), anyDouble());
        }
    }

    @Nested
    @DisplayName("manageJourneys Tests - Advancing Journey")
    class ManageJourneysAdvanceTests {

        @Test
        @DisplayName("Should advance active journey")
        void shouldAdvanceActiveJourney() {
            // Given - Start a journey first
            when(routeUseCase.getRandomRoute()).thenReturn(testRoute);
            JourneyState mockJourneyState = new JourneyState("test-journey", testRoute, DEFAULT_SPEED_MPS);
            mockJourneyState.start();
            when(journeyUseCase.startNewJourney(anyString(), eq(DEFAULT_SPEED_MPS))).thenReturn(mockJourneyState);

            autoJourneySchedulerService.manageJourneys();

            // Setup for advancing
            when(journeyUseCase.journeyExists(anyString())).thenReturn(true);
            when(journeyUseCase.getJourneyState(anyString())).thenReturn(mockJourneyState);
            when(journeyUseCase.advanceJourney(anyString(), anyDouble())).thenReturn(new Coordinate(48.88, 9.17));

            // When
            autoJourneySchedulerService.manageJourneys();

            // Then
            verify(journeyUseCase).advanceJourney(anyString(), eq(UPDATE_INTERVAL_MS / 1000.0));
        }

        @Test
        @DisplayName("Should handle journey not found during advancement")
        void shouldHandleJourneyNotFoundDuringAdvancement() {
            // Given - Start a journey first
            when(routeUseCase.getRandomRoute()).thenReturn(testRoute);
            JourneyState mockJourneyState = new JourneyState("test-journey", testRoute, DEFAULT_SPEED_MPS);
            mockJourneyState.start();
            when(journeyUseCase.startNewJourney(anyString(), eq(DEFAULT_SPEED_MPS))).thenReturn(mockJourneyState);

            autoJourneySchedulerService.manageJourneys();

            // Setup for when journey no longer exists
            when(journeyUseCase.journeyExists(anyString())).thenReturn(false);

            // When
            autoJourneySchedulerService.manageJourneys();

            // Then - Should clear active journey
            assertFalse(autoJourneySchedulerService.hasActiveJourney());
        }
    }

    @Nested
    @DisplayName("manageJourneys Tests - Journey Completion")
    class ManageJourneysCompletionTests {

        @Test
        @DisplayName("Should handle journey completion")
        void shouldHandleJourneyCompletion() {
            // Given - Start a journey first
            when(routeUseCase.getRandomRoute()).thenReturn(testRoute);
            JourneyState mockJourneyState = new JourneyState("test-journey", testRoute, DEFAULT_SPEED_MPS);
            mockJourneyState.start();
            when(journeyUseCase.startNewJourney(anyString(), eq(DEFAULT_SPEED_MPS))).thenReturn(mockJourneyState);

            autoJourneySchedulerService.manageJourneys();

            // Create a completed state
            JourneyState completedState = mock(JourneyState.class);
            when(completedState.getStatus()).thenReturn(JourneyStatus.COMPLETED);

            when(journeyUseCase.journeyExists(anyString())).thenReturn(true);
            when(journeyUseCase.getJourneyState(anyString())).thenReturn(completedState);

            // When
            autoJourneySchedulerService.manageJourneys();

            // Then - Should clear active journey
            assertFalse(autoJourneySchedulerService.hasActiveJourney());
            verify(journeyUseCase).stopJourney(anyString());
        }
    }

    @Nested
    @DisplayName("Helper Method Tests")
    class HelperMethodTests {

        @Test
        @DisplayName("hasActiveJourney should return correct state")
        void hasActiveJourneyShouldReturnCorrectState() {
            // Initially false
            assertFalse(autoJourneySchedulerService.hasActiveJourney());

            // After starting a journey
            when(routeUseCase.getRandomRoute()).thenReturn(testRoute);
            JourneyState mockJourneyState = new JourneyState("test-journey", testRoute, DEFAULT_SPEED_MPS);
            mockJourneyState.start();
            when(journeyUseCase.startNewJourney(anyString(), eq(DEFAULT_SPEED_MPS))).thenReturn(mockJourneyState);

            autoJourneySchedulerService.manageJourneys();

            assertTrue(autoJourneySchedulerService.hasActiveJourney());
        }

        @Test
        @DisplayName("getActiveJourneyId should return journey ID when active")
        void getActiveJourneyIdShouldReturnIdWhenActive() {
            // Given
            when(routeUseCase.getRandomRoute()).thenReturn(testRoute);
            JourneyState mockJourneyState = new JourneyState("test-journey", testRoute, DEFAULT_SPEED_MPS);
            mockJourneyState.start();
            when(journeyUseCase.startNewJourney(anyString(), eq(DEFAULT_SPEED_MPS))).thenReturn(mockJourneyState);

            autoJourneySchedulerService.manageJourneys();

            // When
            Optional<String> journeyId = autoJourneySchedulerService.getActiveJourneyId();

            // Then
            assertTrue(journeyId.isPresent());
            assertTrue(journeyId.get().startsWith("auto-journey-"));
        }

        @Test
        @DisplayName("getActiveJourneyState should return state when active")
        void getActiveJourneyStateShouldReturnStateWhenActive() {
            // Given
            when(routeUseCase.getRandomRoute()).thenReturn(testRoute);
            JourneyState mockJourneyState = new JourneyState("test-journey", testRoute, DEFAULT_SPEED_MPS);
            mockJourneyState.start();
            when(journeyUseCase.startNewJourney(anyString(), eq(DEFAULT_SPEED_MPS))).thenReturn(mockJourneyState);

            autoJourneySchedulerService.manageJourneys();

            when(journeyUseCase.getJourneyState(anyString())).thenReturn(mockJourneyState);

            // When
            Optional<JourneyState> state = autoJourneySchedulerService.getActiveJourneyState();

            // Then
            assertTrue(state.isPresent());
            assertEquals(JourneyStatus.IN_PROGRESS, state.get().getStatus());
        }
    }
}

