package pse.nebula.worldview.infrastructure.adapter.inbound.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing the current state of a journey.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Current state of a journey")
public class JourneyStateDto {

    @JsonProperty("journey_id")
    @Schema(description = "Unique identifier for the journey", example = "journey-1234567890")
    private String journeyId;

    @JsonProperty("route")
    @Schema(description = "The route being traveled")
    private RouteDto route;

    @JsonProperty("current_position")
    @Schema(description = "Current GPS position of the vehicle")
    private CoordinateDto currentPosition;

    @JsonProperty("current_waypoint_index")
    @Schema(description = "Index of the current waypoint in the route", example = "42")
    private int currentWaypointIndex;

    @JsonProperty("status")
    @Schema(description = "Journey status", example = "IN_PROGRESS", allowableValues = {"NOT_STARTED", "IN_PROGRESS", "PAUSED", "COMPLETED"})
    private String status;

    @JsonProperty("speed_meters_per_second")
    @Schema(description = "Current speed in meters per second", example = "13.89")
    private double speedMetersPerSecond;

    @JsonProperty("progress_percentage")
    @Schema(description = "Journey completion percentage (0-100)", example = "45.5")
    private double progressPercentage;
}