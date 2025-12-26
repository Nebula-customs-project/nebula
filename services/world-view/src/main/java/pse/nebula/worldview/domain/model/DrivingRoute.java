package pse.nebula.worldview.domain.model;

import java.util.List;

/**
 * Represents a driving route from a starting location to the dealership.
 * Contains the route metadata and the list of coordinates that form the path.
 */
public record DrivingRoute(
    String id,
    String name,
    String description,
    Coordinate startPoint,
    Coordinate endPoint,
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
        if (startPoint == null) {
            throw new IllegalArgumentException("Start point cannot be null");
        }
        if (endPoint == null) {
            throw new IllegalArgumentException("End point cannot be null");
        }
        if (waypoints == null || waypoints.isEmpty()) {
            throw new IllegalArgumentException("Waypoints cannot be null or empty");
        }
        // Make waypoints immutable
        waypoints = List.copyOf(waypoints);
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

