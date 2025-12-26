package pse.nebula.telemetry.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for starting a simulation.
 * Clean API contract separate from domain model.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartSimulationRequest {

    @NotBlank(message = "Vehicle ID is required")
    @JsonProperty("vehicle_id")
    private String vehicleId;

    @NotNull(message = "Start location is required")
    @JsonProperty("start_location")
    private LocationDto startLocation;

    @NotNull(message = "End location is required")
    @JsonProperty("end_location")
    private LocationDto endLocation;

    @Positive(message = "Speed must be positive")
    @JsonProperty("speed_mps")
    private double speedMps;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationDto {
        @NotNull(message = "Latitude is required")
        @JsonProperty("latitude")
        private Double latitude;

        @NotNull(message = "Longitude is required")
        @JsonProperty("longitude")
        private Double longitude;
    }
}

