package pse.nebula.worldview.infrastructure.adapter.inbound.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * DTO for coordinate update events sent via MQTT.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Real-time coordinate update message (sent via MQTT)")
public class CoordinateUpdateDto {

    @JsonProperty("journey_id")
    @Schema(description = "Journey identifier", example = "journey-1234567890")
    private String journeyId;

    @JsonProperty("coordinate")
    @Schema(description = "Current GPS position")
    private CoordinateDto coordinate;

    @JsonProperty("progress_percentage")
    @Schema(description = "Journey completion percentage (0-100)", example = "45.5")
    private double progressPercentage;

    @JsonProperty("status")
    @Schema(description = "Journey status", example = "IN_PROGRESS")
    private String status;

    @JsonProperty("current_waypoint_index")
    @Schema(description = "Current waypoint index", example = "42")
    private int currentWaypointIndex;

    @JsonProperty("total_waypoints")
    @Schema(description = "Total waypoints in route", example = "150")
    private int totalWaypoints;

    @JsonProperty("timestamp")
    @Schema(description = "Update timestamp (ISO-8601)", example = "2026-01-04T12:00:00Z")
    private Instant timestamp;
}