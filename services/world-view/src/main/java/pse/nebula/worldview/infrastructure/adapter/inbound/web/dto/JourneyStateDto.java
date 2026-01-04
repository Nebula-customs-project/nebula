package pse.nebula.worldview.infrastructure.adapter.inbound.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class JourneyStateDto {

    @JsonProperty("journey_id")
    private String journeyId;

    @JsonProperty("route")
    private RouteDto route;

    @JsonProperty("current_position")
    private CoordinateDto currentPosition;

    @JsonProperty("current_waypoint_index")
    private int currentWaypointIndex;

    @JsonProperty("status")
    private String status;

    @JsonProperty("speed_meters_per_second")
    private double speedMetersPerSecond;

    @JsonProperty("progress_percentage")
    private double progressPercentage;
}