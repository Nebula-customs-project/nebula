package pse.nebula.worldview.infrastructure.adapter.inbound.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO representing a driving route for API responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteDto {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("start_point")
    private CoordinateDto startPoint;

    @JsonProperty("end_point")
    private CoordinateDto endPoint;

    @JsonProperty("waypoints")
    private List<CoordinateDto> waypoints;

    @JsonProperty("total_distance_meters")
    private double totalDistanceMeters;

    @JsonProperty("estimated_duration_seconds")
    private int estimatedDurationSeconds;

    @JsonProperty("total_waypoints")
    private int totalWaypoints;
}