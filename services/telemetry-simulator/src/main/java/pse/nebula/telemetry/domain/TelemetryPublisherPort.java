package pse.nebula.telemetry.domain;

public interface TelemetryPublisherPort {
    void publish(Simulation simulation);
}

