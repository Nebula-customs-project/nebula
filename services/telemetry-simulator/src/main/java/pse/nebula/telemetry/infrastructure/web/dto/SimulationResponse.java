package pse.nebula.telemetry.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for simulation operations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimulationResponse {

    @JsonProperty("vehicle_id")
    private String vehicleId;

    @JsonProperty("status")
    private String status;

    @JsonProperty("current_location")
    private LocationDto currentLocation;

    @JsonProperty("speed_mps")
    private double speedMps;

    @JsonProperty("message")
    private String message;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationDto {
        @JsonProperty("latitude")
        private double latitude;

        @JsonProperty("longitude")
        private double longitude;
    }
}

