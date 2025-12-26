package pse.nebula.telemetry.infrastructure.messaging.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * DTO for telemetry data published to MQTT.
 * Clean separation between domain and wire format.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TelemetryMessageDto {

    @JsonProperty("vehicle_id")
    private String vehicleId;

    @JsonProperty("timestamp")
    private Instant timestamp;

    @JsonProperty("location")
    private LocationDto location;

    @JsonProperty("speed_mps")
    private double speedMps;

    @JsonProperty("status")
    private String status;

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

