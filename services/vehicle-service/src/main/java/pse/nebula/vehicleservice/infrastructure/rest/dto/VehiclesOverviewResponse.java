package pse.nebula.vehicleservice.infrastructure.rest.dto;

import java.util.List;

/**
 * DTO for Cars Overview response containing all vehicles.
 */
public record VehiclesOverviewResponse(
        List<VehicleDto> vehicles
) {
    public static VehiclesOverviewResponse of(List<VehicleDto> vehicles) {
        return new VehiclesOverviewResponse(vehicles);
    }
}

