package pse.nebula.worldview.domain.exception;

/**
 * Exception thrown when a route cannot be found by its ID.
 */
public class RouteNotFoundException extends DomainException {

    private static final String NO_ROUTES_MESSAGE = "No routes available";

    public RouteNotFoundException(String routeId) {
        super("Route not found with ID: " + routeId);
    }

    public RouteNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Private constructor for custom messages.
     */
    private RouteNotFoundException(String message, boolean customMessage) {
        super(message);
    }

    /**
     * Factory method for when no routes are available.
     */
    public static RouteNotFoundException noRoutesAvailable() {
        return new RouteNotFoundException(NO_ROUTES_MESSAGE, true);
    }
}
