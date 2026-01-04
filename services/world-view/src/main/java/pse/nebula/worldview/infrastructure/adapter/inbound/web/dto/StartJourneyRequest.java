package pse.nebula.worldview.infrastructure.adapter.inbound.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Request body for starting a new journey")
public class StartJourneyRequest {

    @NotBlank(message = "Journey ID is required")
    @JsonProperty("journey_id")
    @Schema(description = "Unique identifier for the journey", example = "journey-1234567890", requiredMode = Schema.RequiredMode.REQUIRED)
    private String journeyId;

    @JsonProperty("route_id")
    @Schema(description = "Route identifier. If not provided, a random route will be selected", example = "route-1")
    private String routeId;

    @Positive(message = "Speed must be positive")
    @JsonProperty("speed_meters_per_second")
    @Schema(description = "Travel speed in meters per second", example = "13.89", defaultValue = "13.89")
    private double speedMetersPerSecond;
}