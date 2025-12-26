package pse.nebula.telemetry.infrastructure.persistence.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pse.nebula.telemetry.domain.GeoPoint;
import pse.nebula.telemetry.domain.Route;
import pse.nebula.telemetry.domain.Simulation;
import pse.nebula.telemetry.domain.SimulationStatus;
import pse.nebula.telemetry.infrastructure.persistence.entity.SimulationEntity;
import pse.nebula.telemetry.infrastructure.persistence.entity.SimulationStatusEntity;

import java.util.List;

/**
 * Mapper to convert between Domain Simulation and JPA SimulationEntity.
 * Follows the Hexagonal Architecture principle of keeping domain and infrastructure separate.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SimulationMapper {

    private final ObjectMapper objectMapper;

    /**
     * Converts domain Simulation to JPA entity.
     */
    public SimulationEntity toEntity(Simulation simulation) {
        SimulationEntity entity = new SimulationEntity();
        entity.setVehicleId(simulation.getVehicleId());
        entity.setCurrentLat(simulation.getCurrentLocation().lat());
        entity.setCurrentLng(simulation.getCurrentLocation().lng());
        entity.setSpeed(simulation.getSpeed());
        entity.setStatus(toEntityStatus(simulation.getStatus()));
        entity.setNextRoutePointIndex(simulation.getNextRoutePointIndex());

        // Serialize route points to JSON
        try {
            String routeJson = objectMapper.writeValueAsString(simulation.getRoute().points());
            entity.setRoutePointsJson(routeJson);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize route points for vehicle: {}", simulation.getVehicleId(), e);
            throw new RuntimeException("Failed to serialize route points", e);
        }

        return entity;
    }

    /**
     * Converts JPA entity to domain Simulation.
     */
    public Simulation toDomain(SimulationEntity entity) {
        GeoPoint currentLocation = new GeoPoint(entity.getCurrentLat(), entity.getCurrentLng());

        // Deserialize route points from JSON
        List<GeoPoint> routePoints;
        try {
            routePoints = objectMapper.readValue(
                entity.getRoutePointsJson(),
                new TypeReference<List<GeoPoint>>() {}
            );
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize route points for vehicle: {}", entity.getVehicleId(), e);
            throw new RuntimeException("Failed to deserialize route points", e);
        }

        Route route = new Route(routePoints);

        Simulation simulation = new Simulation(
            entity.getVehicleId(),
            currentLocation,
            entity.getSpeed(),
            route
        );

        // Set the internal state that's not in the constructor
        simulation.setStatus(toDomainStatus(entity.getStatus()));
        simulation.setNextRoutePointIndex(entity.getNextRoutePointIndex());

        return simulation;
    }

    private SimulationStatusEntity toEntityStatus(SimulationStatus status) {
        return SimulationStatusEntity.valueOf(status.name());
    }

    private SimulationStatus toDomainStatus(SimulationStatusEntity status) {
        return SimulationStatus.valueOf(status.name());
    }
}

