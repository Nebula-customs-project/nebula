package pse.nebula.worldview.infrastructure.adapter.persistence.routes;

import pse.nebula.worldview.domain.model.Coordinate;

/**
 * Represents the definition of a driving route.
 * Contains metadata and start/end coordinates for route generation.
 */
public record RouteDefinition(
    String id,
    String name,
    String description,
    Coordinate startPoint,
    Coordinate endPoint,
    int estimatedDistanceMeters,
    int estimatedDurationSeconds
) {
    /**
     * Creates a RouteDefinition with the Porsche Zentrum Stuttgart as the default destination.
     */
    public static RouteDefinition toDealer(
            String id,
            String name,
            String description,
            Coordinate startPoint,
            int estimatedDistanceMeters,
            int estimatedDurationSeconds) {
        return new RouteDefinition(
            id,
            name,
            description,
            startPoint,
            RouteDefinitions.PORSCHE_ZENTRUM_STUTTGART,
            estimatedDistanceMeters,
            estimatedDurationSeconds
        );
    }
}
