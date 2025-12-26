package pse.nebula.telemetry.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pse.nebula.telemetry.domain.Simulation;
import pse.nebula.telemetry.domain.SimulationRepository;
import pse.nebula.telemetry.infrastructure.persistence.entity.SimulationEntity;
import pse.nebula.telemetry.infrastructure.persistence.mapper.SimulationMapper;
import pse.nebula.telemetry.infrastructure.persistence.repository.JpaSimulationRepository;

/**
 * Adapter that implements the SimulationRepository port using Spring Data JPA.
 * This bridges the domain layer with the PostgreSQL database.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JpaSimulationRepositoryAdapter implements SimulationRepository {

    private final JpaSimulationRepository jpaRepository;
    private final SimulationMapper mapper;

    @Override
    public void save(Simulation simulation) {
        try {
            SimulationEntity entity = mapper.toEntity(simulation);
            jpaRepository.save(entity);
            log.debug("Successfully saved simulation for vehicle: {}", simulation.getVehicleId());
        } catch (Exception e) {
            log.error("Failed to save simulation for vehicle: {}", simulation.getVehicleId(), e);
            throw new RuntimeException("Failed to persist simulation", e);
        }
    }

    @Override
    public Simulation findById(String vehicleId) {
        try {
            return jpaRepository.findById(vehicleId)
                .map(mapper::toDomain)
                .orElse(null);
        } catch (Exception e) {
            log.error("Failed to find simulation for vehicle: {}", vehicleId, e);
            throw new RuntimeException("Failed to retrieve simulation", e);
        }
    }
}

