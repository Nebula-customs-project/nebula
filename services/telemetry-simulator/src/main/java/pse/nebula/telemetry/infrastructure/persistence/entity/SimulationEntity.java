package pse.nebula.telemetry.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * JPA Entity for persisting Simulation state.
 * This is the infrastructure layer's representation of a Simulation.
 */
@Entity
@Table(name = "simulations", indexes = {
    @Index(name = "idx_vehicle_id", columnList = "vehicle_id"),
    @Index(name = "idx_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SimulationEntity {

    @Id
    @Column(name = "vehicle_id", nullable = false, unique = true, length = 100)
    private String vehicleId;

    @Column(name = "current_lat", nullable = false)
    private double currentLat;

    @Column(name = "current_lng", nullable = false)
    private double currentLng;

    @Column(name = "speed", nullable = false)
    private double speed;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private SimulationStatusEntity status;

    @Column(name = "route_points", nullable = false, columnDefinition = "TEXT")
    private String routePointsJson;

    @Column(name = "next_route_point_index", nullable = false)
    private int nextRoutePointIndex;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

