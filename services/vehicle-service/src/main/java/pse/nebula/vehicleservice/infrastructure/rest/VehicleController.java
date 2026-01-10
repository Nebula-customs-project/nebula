package pse.nebula.vehicleservice.infrastructure.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pse.nebula.vehicleservice.application.service.ConfigurationService;
import pse.nebula.vehicleservice.application.service.ConfigurationService.VehicleConfiguration;
import pse.nebula.vehicleservice.application.service.VehicleService;
import pse.nebula.vehicleservice.infrastructure.rest.dto.*;

import java.util.List;

/**
 * REST controller for vehicle operations.
 * Provides endpoints for Cars Overview and Car Configurator.
 */
@RestController
@RequestMapping("/api/vehicles")
@Tag(name = "Vehicles", description = "Vehicle and configuration operations")
public class VehicleController {

    private final VehicleService vehicleService;
    private final ConfigurationService configurationService;

    public VehicleController(VehicleService vehicleService, ConfigurationService configurationService) {
        this.vehicleService = vehicleService;
        this.configurationService = configurationService;
    }

    /**
     * Get all vehicles for the Cars Overview page.
     *
     * @return list of all vehicles
     */
    @GetMapping
    @Operation(summary = "Get all vehicles", description = "Returns all available vehicles for the Cars Overview page")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved vehicles",
                    content = @Content(schema = @Schema(implementation = VehiclesOverviewResponse.class)))
    })
    public ResponseEntity<VehiclesOverviewResponse> getAllVehicles() {
        List<VehicleDto> vehicles = vehicleService.getAllVehicles().stream()
                .map(VehicleDto::fromEntity)
                .toList();

        return ResponseEntity.ok(VehiclesOverviewResponse.of(vehicles));
    }

    /**
     * Get a specific vehicle by ID.
     *
     * @param id the vehicle ID
     * @return the vehicle details
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get vehicle by ID", description = "Returns a specific vehicle by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved vehicle",
                    content = @Content(schema = @Schema(implementation = VehicleDto.class))),
            @ApiResponse(responseCode = "404", description = "Vehicle not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<VehicleDto> getVehicleById(@PathVariable Integer id) {
        VehicleDto vehicle = VehicleDto.fromEntity(vehicleService.getVehicleById(id));
        return ResponseEntity.ok(vehicle);
    }

    /**
     * Get configuration options for a specific vehicle.
     * Returns all paints, rims, and interiors with prices resolved for the vehicle's car type.
     *
     * @param id the vehicle ID
     * @return configuration options with resolved prices
     */
    @GetMapping("/{id}/configuration")
    @Operation(summary = "Get configuration options",
               description = "Returns all configuration options (paints, rims, interiors) with prices resolved for the vehicle's car type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved configuration",
                    content = @Content(schema = @Schema(implementation = ConfigurationResponse.class))),
            @ApiResponse(responseCode = "404", description = "Vehicle not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ConfigurationResponse> getVehicleConfiguration(@PathVariable Integer id) {
        VehicleConfiguration configuration = configurationService.getConfigurationForVehicle(id);
        return ResponseEntity.ok(ConfigurationResponse.fromVehicleConfiguration(configuration));
    }
}

