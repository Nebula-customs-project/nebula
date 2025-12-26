package pse.nebula.telemetry.application.exception;

/**
 * Exception thrown when a simulation cannot be found.
 */
public class SimulationNotFoundException extends RuntimeException {

    public SimulationNotFoundException(String message) {
        super(message);
    }
}

