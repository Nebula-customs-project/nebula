package pse.nebula.worldview.infrastructure.adapter.inbound.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for starting a new journey.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartJourneyRequest {

    @NotBlank(message = "Journey ID is required")
    @JsonProperty("journey_id")
    private String journeyId;

    @JsonProperty("route_id")
    private String routeId;  // If not provided, a random route will be selected

    @Positive(message = "Speed must be positive")
    @JsonProperty("speed_meters_per_second")
    private double speedMetersPerSecond;
}