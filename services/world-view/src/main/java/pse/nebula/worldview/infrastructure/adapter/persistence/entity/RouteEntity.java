package pse.nebula.worldview.infrastructure.adapter.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA Entity for storing driving routes in the database.
 */
@Entity
@Table(name = "driving_routes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteEntity {

    @Id
    @Column(name = "route_id", nullable = false, unique = true)
    private String routeId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "start_latitude", nullable = false)
    private Double startLatitude;

    @Column(name = "start_longitude", nullable = false)
    private Double startLongitude;

    @Column(name = "end_latitude", nullable = false)
    private Double endLatitude;

    @Column(name = "end_longitude", nullable = false)
    private Double endLongitude;

    @Column(name = "total_distance_meters", nullable = false)
    private Double totalDistanceMeters;

    @Column(name = "estimated_duration_seconds", nullable = false)
    private Integer estimatedDurationSeconds;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("sequenceOrder ASC")
    @Builder.Default
    private List<WaypointEntity> waypoints = new ArrayList<>();

    /**
     * Helper method to add waypoint with proper bidirectional relationship
     */
    public void addWaypoint(WaypointEntity waypoint) {
        waypoints.add(waypoint);
        waypoint.setRoute(this);
    }
}

