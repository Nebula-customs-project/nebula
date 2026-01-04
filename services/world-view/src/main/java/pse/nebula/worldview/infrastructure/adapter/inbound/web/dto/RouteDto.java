package pse.nebula.worldview.infrastructure.adapter.inbound.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "A driving route from a starting point to the dealership")
public class RouteDto {

    @JsonProperty("id")
    @Schema(description = "Unique route identifier", example = "route-1")
    private String id;

    @JsonProperty("name")
    @Schema(description = "Human-readable route name", example = "Ludwigsburg Route")
    private String name;

    @JsonProperty("description")
    @Schema(description = "Route description", example = "From Ludwigsburg Schloss to Dealership")
    private String description;

    @JsonProperty("start_point")
    @Schema(description = "Starting GPS coordinates")
    private CoordinateDto startPoint;

    @JsonProperty("end_point")
    @Schema(description = "Destination GPS coordinates (dealership)")
    private CoordinateDto endPoint;

    @JsonProperty("waypoints")
    @Schema(description = "List of GPS waypoints along the route")
    private List<CoordinateDto> waypoints;

    @JsonProperty("total_distance_meters")
    @Schema(description = "Total route distance in meters", example = "15234.5")
    private double totalDistanceMeters;

    @JsonProperty("estimated_duration_seconds")
    @Schema(description = "Estimated travel duration in seconds", example = "1200")
    private int estimatedDurationSeconds;

    @JsonProperty("total_waypoints")
    @Schema(description = "Number of waypoints in the route", example = "150")
    private int totalWaypoints;
}