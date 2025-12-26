package pse.nebula.telemetry.application.command;

import pse.nebula.telemetry.domain.GeoPoint;

/**
 * Command for starting a new vehicle simulation.
 *
 * @param vehicleId Unique identifier for the vehicle
 * @param startLocation Starting point coordinates
 * @param endLocation Destination point coordinates
 * @param speedMps Speed in meters per second
 */
public record StartSimulationCommand(
    String vehicleId,
    GeoPoint startLocation,
    GeoPoint endLocation,
    double speedMps
) {
    public StartSimulationCommand {
        if (vehicleId == null || vehicleId.isBlank()) {
            throw new IllegalArgumentException("Vehicle ID cannot be null or empty");
        }
        if (startLocation == null) {
            throw new IllegalArgumentException("Start location cannot be null");
        }
        if (endLocation == null) {
            throw new IllegalArgumentException("End location cannot be null");
        }
        if (speedMps <= 0) {
            throw new IllegalArgumentException("Speed must be greater than 0");
        }
    }
}

