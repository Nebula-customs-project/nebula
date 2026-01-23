package pse.nebula.uservehicleservice.infrastructure.adapter.outbound.websocket.dto;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO representing vehicle telemetry data sent via WebSocket.
 */
public record VehicleTelemetryDto(
        String vehicleName,
        LocationDto location,
        BigDecimal fuel,
        Instant timestamp) {
    /**
     * DTO representing vehicle location coordinates.
     */
    public record LocationDto(
            double lat,
            double lng) {
    }
}
