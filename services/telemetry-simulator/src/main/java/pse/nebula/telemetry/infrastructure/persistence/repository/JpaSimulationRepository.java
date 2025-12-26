package pse.nebula.telemetry.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pse.nebula.telemetry.infrastructure.persistence.entity.SimulationEntity;

/**
 * Spring Data JPA repository for SimulationEntity.
 */
@Repository
public interface JpaSimulationRepository extends JpaRepository<SimulationEntity, String> {
}

