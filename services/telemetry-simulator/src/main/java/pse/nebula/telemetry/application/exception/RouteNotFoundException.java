package pse.nebula.telemetry.application.exception;

/**
 * Exception thrown when a route cannot be found between two points.
 */
public class RouteNotFoundException extends RuntimeException {

    public RouteNotFoundException(String message) {
        super(message);
    }

    public RouteNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

