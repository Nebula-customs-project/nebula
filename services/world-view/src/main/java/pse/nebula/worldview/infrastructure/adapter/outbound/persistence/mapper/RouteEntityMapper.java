package pse.nebula.worldview.infrastructure.adapter.outbound.persistence.mapper;

import org.springframework.stereotype.Component;
import pse.nebula.worldview.domain.model.Coordinate;
import pse.nebula.worldview.domain.model.DrivingRoute;
import pse.nebula.worldview.infrastructure.adapter.outbound.persistence.entity.RouteEntity;
import pse.nebula.worldview.infrastructure.adapter.outbound.persistence.entity.WaypointEntity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Mapper for converting between RouteEntity and DrivingRoute domain model.
 */
@Component
public class RouteEntityMapper {

    /**
     * Convert a RouteEntity to a DrivingRoute domain model.
     *
     * @param entity The JPA entity
     * @return The domain model
     */
    public DrivingRoute toDomain(RouteEntity entity) {
        if (entity == null) {
            return null;
        }

        List<Coordinate> waypoints = entity.getWaypoints().stream()
                .sorted(Comparator.comparingInt(WaypointEntity::getSequenceOrder))
                .map(wp -> new Coordinate(wp.getLatitude(), wp.getLongitude()))
                .toList();

        return new DrivingRoute(
                entity.getRouteId(),
                entity.getName(),
                entity.getDescription(),
                waypoints,
                entity.getTotalDistanceMeters(),
                entity.getEstimatedDurationSeconds()
        );
    }

    /**
     * Convert a DrivingRoute domain model to a RouteEntity.
     *
     * @param route The domain model
     * @return The JPA entity
     */
    public RouteEntity toEntity(DrivingRoute route) {
        if (route == null) {
            return null;
        }

        RouteEntity entity = RouteEntity.builder()
                .routeId(route.id())
                .name(route.name())
                .description(route.description())
                .startLatitude(route.startPoint().latitude())
                .startLongitude(route.startPoint().longitude())
                .endLatitude(route.endPoint().latitude())
                .endLongitude(route.endPoint().longitude())
                .totalDistanceMeters(route.totalDistanceMeters())
                .estimatedDurationSeconds(route.estimatedDurationSeconds())
                .waypoints(new ArrayList<>())
                .build();

        // Add waypoints with sequence order
        List<Coordinate> waypoints = route.waypoints();
        for (int i = 0; i < waypoints.size(); i++) {
            Coordinate coord = waypoints.get(i);
            WaypointEntity waypointEntity = WaypointEntity.builder()
                    .latitude(coord.latitude())
                    .longitude(coord.longitude())
                    .sequenceOrder(i)
                    .build();
            entity.addWaypoint(waypointEntity);
        }

        return entity;
    }
}

