package pse.nebula.telemetry.domain;

public interface SimulationRepository {
    void save(Simulation simulation);
    Simulation findById(String vehicleId);
}

