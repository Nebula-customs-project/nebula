package pse.nebula.worldview.infrastructure.adapter.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * DTO for coordinate update events sent via SSE.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoordinateUpdateDto {

    @JsonProperty("journey_id")
    private String journeyId;

    @JsonProperty("coordinate")
    private CoordinateDto coordinate;

    @JsonProperty("progress_percentage")
    private double progressPercentage;

    @JsonProperty("status")
    private String status;

    @JsonProperty("current_waypoint_index")
    private int currentWaypointIndex;

    @JsonProperty("total_waypoints")
    private int totalWaypoints;

    @JsonProperty("timestamp")
    private Instant timestamp;
}

