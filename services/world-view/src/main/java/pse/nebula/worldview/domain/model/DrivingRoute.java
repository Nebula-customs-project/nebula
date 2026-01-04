package pse.nebula.worldview.domain.model;

import java.util.List;
import java.util.Objects;

/**
 * Represents a driving route from a starting location to the dealership.
 * Contains the route metadata and the list of coordinates that form the path.
 * Immutable value object following Domain-Driven Design principles.
 */
public record DrivingRoute(
    String id,
    String name,
    String description,
    List<Coordinate> waypoints,
    double totalDistanceMeters,
    int estimatedDurationSeconds
) {
    public DrivingRoute {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Route ID cannot be null or empty");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Route name cannot be null or empty");
        }
        if (waypoints == null || waypoints.size() < 2) {
            throw new IllegalArgumentException("Route must have at least 2 waypoints (start and end)");
        }
        // Validate no null waypoints
        if (waypoints.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Waypoints list cannot contain null elements");
        }
        if (!Double.isFinite(totalDistanceMeters) || totalDistanceMeters <= 0) {
            throw new IllegalArgumentException(
                "Total distance must be a positive finite number, got: " + totalDistanceMeters);
        }
        if (estimatedDurationSeconds <= 0) {
            throw new IllegalArgumentException(
                "Estimated duration must be positive, got: " + estimatedDurationSeconds);
        }
        // Make waypoints immutable
        waypoints = List.copyOf(waypoints);
    }

    /**
     * Get the starting point of the route (first waypoint).
     *
     * @return The starting coordinate
     */
    public Coordinate startPoint() {
        return waypoints.get(0);
    }

    /**
     * Get the ending point of the route (last waypoint).
     *
     * @return The ending coordinate
     */
    public Coordinate endPoint() {
        return waypoints.get(waypoints.size() - 1);
    }

    /**
     * Get the total number of waypoints including start and end.
     */
    public int getTotalWaypoints() {
        return waypoints.size();
    }

    /**
     * Get a waypoint at a specific index.
     *
     * @param index The index of the waypoint
     * @return The coordinate at the given index
     */
    public Coordinate getWaypointAt(int index) {
        if (index < 0 || index >= waypoints.size()) {
            throw new IndexOutOfBoundsException("Waypoint index out of bounds: " + index);
        }
        return waypoints.get(index);
    }
}