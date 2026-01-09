package pse.nebula.worldview.infrastructure.adapter.inbound.web.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pse.nebula.worldview.domain.model.Coordinate;
import pse.nebula.worldview.domain.model.DrivingRoute;
import pse.nebula.worldview.domain.model.JourneyState;
import pse.nebula.worldview.infrastructure.adapter.inbound.web.dto.CoordinateDto;
import pse.nebula.worldview.infrastructure.adapter.inbound.web.dto.CoordinateUpdateDto;
import pse.nebula.worldview.infrastructure.adapter.inbound.web.dto.JourneyStateDto;
import pse.nebula.worldview.infrastructure.adapter.inbound.web.dto.RouteDto;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DtoMapper.
 * Tests the conversion between domain models and DTOs.
 */
@DisplayName("DtoMapper Unit Tests")
class DtoMapperTest {

    private DtoMapper dtoMapper;
    private DrivingRoute testRoute;
    private Coordinate testCoordinate;

    @BeforeEach
    void setUp() {
        dtoMapper = new DtoMapper();

        testCoordinate = new Coordinate(48.8973, 9.1920);

        List<Coordinate> waypoints = Arrays.asList(
                new Coordinate(48.8973, 9.1920),
                new Coordinate(48.8800, 9.1750),
                new Coordinate(48.8600, 9.1600),
                new Coordinate(48.8354, 9.1520)
        );

        testRoute = new DrivingRoute(
                "route-1",
                "Ludwigsburg Schloss Route",
                "From Ludwigsburg Palace via B27",
                waypoints,
                15000,
                1200
        );
    }

    @Nested
    @DisplayName("toDto(Coordinate) Tests")
    class CoordinateToDtoTests {

        @Test
        @DisplayName("Should correctly map Coordinate to CoordinateDto")
        void shouldMapCoordinateToDto() {
            // When
            CoordinateDto result = dtoMapper.toDto(testCoordinate);

            // Then
            assertNotNull(result);
            assertEquals(48.8973, result.getLatitude());
            assertEquals(9.1920, result.getLongitude());
        }

        @Test
        @DisplayName("Should handle edge case coordinates")
        void shouldHandleEdgeCaseCoordinates() {
            // Given
            Coordinate edgeCoordinate = new Coordinate(-90.0, -180.0);

            // When
            CoordinateDto result = dtoMapper.toDto(edgeCoordinate);

            // Then
            assertEquals(-90.0, result.getLatitude());
            assertEquals(-180.0, result.getLongitude());
        }
    }

    @Nested
    @DisplayName("toDto(DrivingRoute) Tests")
    class RouteToDtoTests {

        @Test
        @DisplayName("Should correctly map DrivingRoute to RouteDto")
        void shouldMapRouteToDto() {
            // When
            RouteDto result = dtoMapper.toDto(testRoute);

            // Then
            assertNotNull(result);
            assertEquals("route-1", result.getId());
            assertEquals("Ludwigsburg Schloss Route", result.getName());
            assertEquals("From Ludwigsburg Palace via B27", result.getDescription());
            assertEquals(15000, result.getTotalDistanceMeters());
            assertEquals(1200, result.getEstimatedDurationSeconds());
            assertEquals(4, result.getTotalWaypoints());
        }

        @Test
        @DisplayName("Should correctly map start and end points")
        void shouldMapStartAndEndPoints() {
            // When
            RouteDto result = dtoMapper.toDto(testRoute);

            // Then
            assertNotNull(result.getStartPoint());
            assertEquals(48.8973, result.getStartPoint().getLatitude());
            assertEquals(9.1920, result.getStartPoint().getLongitude());

            assertNotNull(result.getEndPoint());
            assertEquals(48.8354, result.getEndPoint().getLatitude());
            assertEquals(9.1520, result.getEndPoint().getLongitude());
        }

        @Test
        @DisplayName("Should correctly map all waypoints")
        void shouldMapAllWaypoints() {
            // When
            RouteDto result = dtoMapper.toDto(testRoute);

            // Then
            assertNotNull(result.getWaypoints());
            assertEquals(4, result.getWaypoints().size());
            assertEquals(48.8973, result.getWaypoints().get(0).getLatitude());
            assertEquals(48.8354, result.getWaypoints().get(3).getLatitude());
        }
    }

    @Nested
    @DisplayName("toDto(JourneyState) Tests")
    class JourneyStateToDtoTests {

        @Test
        @DisplayName("Should correctly map JourneyState to JourneyStateDto")
        void shouldMapJourneyStateToDto() {
            // Given
            JourneyState journeyState = new JourneyState("journey-1", testRoute, 13.89);
            journeyState.start();

            // When
            JourneyStateDto result = dtoMapper.toDto(journeyState);

            // Then
            assertNotNull(result);
            assertEquals("journey-1", result.getJourneyId());
            assertEquals("IN_PROGRESS", result.getStatus());
            assertEquals(13.89, result.getSpeedMetersPerSecond());
            assertNotNull(result.getCurrentPosition());
            assertNotNull(result.getRoute());
        }

        @Test
        @DisplayName("Should correctly map journey progress")
        void shouldMapJourneyProgress() {
            // Given
            JourneyState journeyState = new JourneyState("journey-1", testRoute, 13.89);

            // When
            JourneyStateDto result = dtoMapper.toDto(journeyState);

            // Then
            assertEquals(0, result.getCurrentWaypointIndex());
            assertEquals(0.0, result.getProgressPercentage(), 0.01);
        }

        @Test
        @DisplayName("Should correctly map paused journey status")
        void shouldMapPausedJourneyStatus() {
            // Given
            JourneyState journeyState = new JourneyState("journey-1", testRoute, 13.89);
            journeyState.start();
            journeyState.pause();

            // When
            JourneyStateDto result = dtoMapper.toDto(journeyState);

            // Then
            assertEquals("PAUSED", result.getStatus());
        }
    }

    @Nested
    @DisplayName("toCoordinateUpdate Tests")
    class ToCoordinateUpdateTests {

        @Test
        @DisplayName("Should correctly create CoordinateUpdateDto")
        void shouldCreateCoordinateUpdateDto() {
            // Given
            JourneyState journeyState = new JourneyState("journey-1", testRoute, 13.89);
            journeyState.start();

            // When
            CoordinateUpdateDto result = dtoMapper.toCoordinateUpdate(journeyState);

            // Then
            assertNotNull(result);
            assertEquals("journey-1", result.getJourneyId());
            assertEquals("IN_PROGRESS", result.getStatus());
            assertNotNull(result.getCoordinate());
            assertNotNull(result.getTimestamp());
            assertEquals(4, result.getTotalWaypoints());
        }

        @Test
        @DisplayName("Should include timestamp in update")
        void shouldIncludeTimestamp() {
            // Given
            JourneyState journeyState = new JourneyState("journey-1", testRoute, 13.89);
            journeyState.start();

            // When
            CoordinateUpdateDto result = dtoMapper.toCoordinateUpdate(journeyState);

            // Then
            assertNotNull(result.getTimestamp());
        }
    }
}

