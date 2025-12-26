package pse.nebula.worldview.infrastructure.adapter.web.mapper;

import org.springframework.stereotype.Component;
import pse.nebula.worldview.domain.model.Coordinate;
import pse.nebula.worldview.domain.model.DrivingRoute;
import pse.nebula.worldview.domain.model.JourneyState;
import pse.nebula.worldview.infrastructure.adapter.web.dto.CoordinateDto;
import pse.nebula.worldview.infrastructure.adapter.web.dto.CoordinateUpdateDto;
import pse.nebula.worldview.infrastructure.adapter.web.dto.JourneyStateDto;
import pse.nebula.worldview.infrastructure.adapter.web.dto.RouteDto;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting between domain models and DTOs.
 */
@Component
public class DtoMapper {

    /**
     * Convert a Coordinate domain model to DTO.
     */
    public CoordinateDto toDto(Coordinate coordinate) {
        return CoordinateDto.builder()
            .latitude(coordinate.latitude())
            .longitude(coordinate.longitude())
            .build();
    }

    /**
     * Convert a DrivingRoute domain model to DTO.
     */
    public RouteDto toDto(DrivingRoute route) {
        List<CoordinateDto> waypointDtos = route.waypoints().stream()
            .map(this::toDto)
            .collect(Collectors.toList());

        return RouteDto.builder()
            .id(route.id())
            .name(route.name())
            .description(route.description())
            .startPoint(toDto(route.startPoint()))
            .endPoint(toDto(route.endPoint()))
            .waypoints(waypointDtos)
            .totalDistanceMeters(route.totalDistanceMeters())
            .estimatedDurationSeconds(route.estimatedDurationSeconds())
            .totalWaypoints(route.getTotalWaypoints())
            .build();
    }

    /**
     * Convert a JourneyState domain model to DTO.
     */
    public JourneyStateDto toDto(JourneyState journeyState) {
        return JourneyStateDto.builder()
            .journeyId(journeyState.getJourneyId())
            .route(toDto(journeyState.getRoute()))
            .currentPosition(toDto(journeyState.getCurrentPosition()))
            .currentWaypointIndex(journeyState.getCurrentWaypointIndex())
            .status(journeyState.getStatus().name())
            .speedMetersPerSecond(journeyState.getSpeedMetersPerSecond())
            .progressPercentage(journeyState.getProgressPercentage())
            .build();
    }

    /**
     * Create a coordinate update DTO for SSE events.
     */
    public CoordinateUpdateDto toCoordinateUpdate(JourneyState journeyState) {
        return CoordinateUpdateDto.builder()
            .journeyId(journeyState.getJourneyId())
            .coordinate(toDto(journeyState.getCurrentPosition()))
            .progressPercentage(journeyState.getProgressPercentage())
            .status(journeyState.getStatus().name())
            .currentWaypointIndex(journeyState.getCurrentWaypointIndex())
            .totalWaypoints(journeyState.getRoute().getTotalWaypoints())
            .timestamp(Instant.now())
            .build();
    }
}

