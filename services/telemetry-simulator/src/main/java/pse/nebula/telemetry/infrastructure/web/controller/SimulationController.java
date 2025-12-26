package pse.nebula.telemetry.infrastructure.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pse.nebula.telemetry.application.command.StartSimulationCommand;
import pse.nebula.telemetry.application.service.SimulationRunnerService;
import pse.nebula.telemetry.domain.GeoPoint;
import pse.nebula.telemetry.domain.Simulation;
import pse.nebula.telemetry.infrastructure.web.dto.SimulationResponse;
import pse.nebula.telemetry.infrastructure.web.dto.StartSimulationRequest;

/**
 * REST API controller for simulation operations.
 * Provides endpoints to start, stop, and query simulations.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/simulations")
@RequiredArgsConstructor
public class SimulationController {

    private final SimulationRunnerService simulationService;

    /**
     * Start a new vehicle simulation.
     */
    @PostMapping
    public ResponseEntity<SimulationResponse> startSimulation(
            @Valid @RequestBody StartSimulationRequest request) {

        log.info("Received request to start simulation for vehicle: {}", request.getVehicleId());

        // Convert request DTO to command
        StartSimulationCommand command = new StartSimulationCommand(
            request.getVehicleId(),
            new GeoPoint(request.getStartLocation().getLatitude(), request.getStartLocation().getLongitude()),
            new GeoPoint(request.getEndLocation().getLatitude(), request.getEndLocation().getLongitude()),
            request.getSpeedMps()
        );

        // Execute command
        simulationService.startSimulation(command);

        // Build response
        SimulationResponse response = SimulationResponse.builder()
            .vehicleId(request.getVehicleId())
            .status("RUNNING")
            .currentLocation(SimulationResponse.LocationDto.builder()
                .latitude(request.getStartLocation().getLatitude())
                .longitude(request.getStartLocation().getLongitude())
                .build())
            .speedMps(request.getSpeedMps())
            .message("Simulation started successfully")
            .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Stop a running simulation.
     */
    @PostMapping("/{vehicleId}/stop")
    public ResponseEntity<SimulationResponse> stopSimulation(@PathVariable String vehicleId) {
        log.info("Received request to stop simulation for vehicle: {}", vehicleId);

        simulationService.stopSimulation(vehicleId);

        SimulationResponse response = SimulationResponse.builder()
            .vehicleId(vehicleId)
            .status("STOPPED")
            .message("Simulation stopped successfully")
            .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Get the current status of a simulation.
     */
    @GetMapping("/{vehicleId}")
    public ResponseEntity<SimulationResponse> getSimulation(@PathVariable String vehicleId) {
        log.info("Received request to get simulation for vehicle: {}", vehicleId);

        Simulation simulation = simulationService.getSimulation(vehicleId);

        SimulationResponse response = SimulationResponse.builder()
            .vehicleId(simulation.getVehicleId())
            .status(simulation.getStatus().name())
            .currentLocation(SimulationResponse.LocationDto.builder()
                .latitude(simulation.getCurrentLocation().lat())
                .longitude(simulation.getCurrentLocation().lng())
                .build())
            .speedMps(simulation.getSpeed())
            .build();

        return ResponseEntity.ok(response);
    }
}

