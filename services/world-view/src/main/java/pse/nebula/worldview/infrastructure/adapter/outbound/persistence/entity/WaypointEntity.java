package pse.nebula.worldview.infrastructure.adapter.outbound.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA Entity for storing waypoints (coordinates) of a route.
 */
@Entity
@Table(name = "waypoints")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaypointEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "sequence_order", nullable = false)
    private Integer sequenceOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private RouteEntity route;
}

