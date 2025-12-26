package pse.nebula.telemetry.application.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pse.nebula.telemetry.application.command.StartSimulationCommand;
import pse.nebula.telemetry.application.exception.RouteNotFoundException;
import pse.nebula.telemetry.domain.*;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Application service responsible for orchestrating vehicle simulations.
 * Manages the simulation lifecycle and coordinates between domain logic and infrastructure.
 *
 * This service runs a scheduled task every 7 seconds to move all active simulations
 * and publish telemetry data.
 */
@Slf4j
@Service
public class SimulationRunnerService {

    private static final double UPDATE_INTERVAL_SECONDS = 7.0;

    private final SimulationRepository simulationRepository;
    private final RouteServicePort routeService;
    private final TelemetryPublisherPort telemetryPublisher;

    // Thread-safe list to track active simulations
    private final List<String> activeSimulationIds = new CopyOnWriteArrayList<>();

    public SimulationRunnerService(
            SimulationRepository simulationRepository,
            RouteServicePort routeService,
            TelemetryPublisherPort telemetryPublisher) {
        this.simulationRepository = simulationRepository;
        this.routeService = routeService;
        this.telemetryPublisher = telemetryPublisher;
    }

    /**
     * Starts a new simulation for a vehicle.
     *
     * @param command Command containing simulation parameters
     * @throws RouteNotFoundException if no route can be found between start and end points
     * @throws IllegalArgumentException if command validation fails
     */
    @Transactional
    public void startSimulation(StartSimulationCommand command) {
        log.info("Starting simulation for vehicle: {}", command.vehicleId());

        try {
            // Fetch route from the route service
            Route route = fetchRoute(command.startLocation(), command.endLocation());

            // Create and initialize the simulation
            Simulation simulation = createSimulation(command, route);
            simulation.start();

            // Persist the simulation
            simulationRepository.save(simulation);

            // Track the simulation for scheduled updates
            if (!activeSimulationIds.contains(command.vehicleId())) {
                activeSimulationIds.add(command.vehicleId());
            }

            log.info("Successfully started simulation for vehicle: {}", command.vehicleId());

        } catch (Exception e) {
            log.error("Failed to start simulation for vehicle: {}", command.vehicleId(), e);
            throw new RouteNotFoundException(
                String.format("Unable to start simulation for vehicle %s: %s",
                    command.vehicleId(), e.getMessage()), e);
        }
    }

    /**
     * Scheduled task that runs every 7 seconds to update all active simulations.
     * For each active simulation:
     * 1. Calculates distance traveled based on speed and time
     * 2. Updates the simulation's location using domain logic
     * 3. Publishes telemetry data
     * 4. Persists the updated state
     */
    @Scheduled(fixedRate = 7000) // 7 seconds in milliseconds
    @Transactional
    public void updateActiveSimulations() {
        if (activeSimulationIds.isEmpty()) {
            return;
        }

        log.debug("Updating {} active simulation(s)", activeSimulationIds.size());

        // Create a snapshot to avoid concurrent modification
        List<String> simulationsToProcess = List.copyOf(activeSimulationIds);

        for (String vehicleId : simulationsToProcess) {
            try {
                processSimulation(vehicleId);
            } catch (Exception e) {
                log.error("Error processing simulation for vehicle: {}", vehicleId, e);
                // Continue processing other simulations even if one fails
            }
        }
    }

    /**
     * Stops a running simulation.
     *
     * @param vehicleId The vehicle identifier
     */
    @Transactional
    public void stopSimulation(String vehicleId) {
        log.info("Stopping simulation for vehicle: {}", vehicleId);

        Simulation simulation = simulationRepository.findById(vehicleId);
        if (simulation == null) {
            log.warn("Cannot stop simulation - vehicle not found: {}", vehicleId);
            return;
        }

        simulation.stop();
        simulationRepository.save(simulation);
        activeSimulationIds.remove(vehicleId);

        log.info("Successfully stopped simulation for vehicle: {}", vehicleId);
    }

    /**
     * Gets the current status of a simulation.
     *
     * @param vehicleId The vehicle identifier
     * @return The current simulation state
     */
    @Transactional(readOnly = true)
    public Simulation getSimulation(String vehicleId) {
        Simulation simulation = simulationRepository.findById(vehicleId);
        if (simulation == null) {
            throw new pse.nebula.telemetry.application.exception.SimulationNotFoundException(
                String.format("Simulation not found for vehicle ID: %s", vehicleId));
        }
        return simulation;
    }

    // ===== Private Helper Methods =====

    /**
     * Processes a single simulation update.
     * Follows the DRY principle by extracting common logic.
     */
    private void processSimulation(String vehicleId) {
        Simulation simulation = simulationRepository.findById(vehicleId);

        if (simulation == null) {
            log.warn("Simulation not found for vehicle: {}", vehicleId);
            activeSimulationIds.remove(vehicleId);
            return;
        }

        // Only process running simulations
        if (simulation.getStatus() != SimulationStatus.RUNNING) {
            handleInactiveSimulation(vehicleId, simulation);
            return;
        }

        // Move the vehicle based on elapsed time
        simulation.move(UPDATE_INTERVAL_SECONDS);

        // Publish telemetry data
        publishTelemetry(simulation);

        // Save the updated state
        simulationRepository.save(simulation);

        // Check if simulation has completed
        if (simulation.getStatus() == SimulationStatus.COMPLETED) {
            handleCompletedSimulation(vehicleId, simulation);
        }

        log.debug("Updated simulation for vehicle: {} at location: {}",
            vehicleId, simulation.getCurrentLocation());
    }

    /**
     * Fetches a route from the route service with proper error handling.
     */
    private Route fetchRoute(pse.nebula.telemetry.domain.GeoPoint start, pse.nebula.telemetry.domain.GeoPoint end) {
        try {
            Route route = routeService.getRoute(start, end);

            if (route == null || route.points() == null || route.points().isEmpty()) {
                throw new RouteNotFoundException(
                    String.format("No route found between points: %s and %s", start, end));
            }

            return route;

        } catch (RouteNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RouteNotFoundException(
                String.format("Error fetching route between points: %s and %s", start, end), e);
        }
    }

    /**
     * Creates a new simulation instance from the command.
     */
    private Simulation createSimulation(StartSimulationCommand command, Route route) {
        return new Simulation(
            command.vehicleId(),
            command.startLocation(),
            command.speedMps(),
            route
        );
    }

    /**
     * Publishes telemetry data with error handling.
     */
    private void publishTelemetry(Simulation simulation) {
        try {
            telemetryPublisher.publish(simulation);
        } catch (Exception e) {
            log.error("Failed to publish telemetry for vehicle: {}",
                simulation.getVehicleId(), e);
            // Don't throw - we don't want to stop the simulation due to publishing failures
        }
    }

    /**
     * Handles simulations that are no longer active.
     */
    private void handleInactiveSimulation(String vehicleId, Simulation simulation) {
        log.debug("Simulation for vehicle {} is no longer active: {}",
            vehicleId, simulation.getStatus());
        activeSimulationIds.remove(vehicleId);
    }

    /**
     * Handles completed simulations.
     */
    private void handleCompletedSimulation(String vehicleId, Simulation simulation) {
        log.info("Simulation completed for vehicle: {}", vehicleId);
        activeSimulationIds.remove(vehicleId);

        // Publish final telemetry
        publishTelemetry(simulation);
    }
}

