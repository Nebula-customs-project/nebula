package pse.nebula.vehicleservice.infrastructure.rest.dto;

import pse.nebula.vehicleservice.application.service.ConfigurationService.VehicleConfiguration;
import pse.nebula.vehicleservice.domain.model.CarType;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for configuration options response.
 * Contains all configuration options with prices resolved for the vehicle's car type.
 */
public record ConfigurationResponse(
        Integer vehicleId,
        CarType carType,
        BigDecimal basePrice,
        List<PaintOptionDto> paints,
        List<RimOptionDto> rims,
        List<InteriorOptionDto> interiors
) {
    /**
     * Creates a ConfigurationResponse from a VehicleConfiguration.
     */
    public static ConfigurationResponse fromVehicleConfiguration(VehicleConfiguration config) {
        CarType carType = config.carType();

        List<PaintOptionDto> paintDtos = config.paints().stream()
                .map(paint -> PaintOptionDto.fromEntity(paint, carType))
                .toList();

        List<RimOptionDto> rimDtos = config.rims().stream()
                .map(rim -> RimOptionDto.fromEntity(rim, carType))
                .toList();

        List<InteriorOptionDto> interiorDtos = config.interiors().stream()
                .map(interior -> InteriorOptionDto.fromEntity(interior, carType))
                .toList();

        return new ConfigurationResponse(
                config.vehicle().getId(),
                carType,
                config.vehicle().getBasePrice(),
                paintDtos,
                rimDtos,
                interiorDtos
        );
    }
}

