package pse.nebula.worldview.domain.port.inbound;

import pse.nebula.worldview.domain.model.DrivingRoute;

import java.util.List;

/**
 * Inbound port for route-related use cases.
 * This is the primary port that the application layer uses.
 */
public interface RouteUseCase {

    /**
     * Get all available routes to the dealership.
     *
     * @return List of all predefined driving routes
     */
    List<DrivingRoute> getAllRoutes();

    /**
     * Get a specific route by its ID.
     *
     * @param routeId The route identifier
     * @return The driving route
     */
    DrivingRoute getRouteById(String routeId);

    /**
     * Get a random route for a new journey.
     * This is called on each UI reload.
     *
     * @return A randomly selected driving route
     */
    DrivingRoute getRandomRoute();

    /**
     * Get the total number of available routes.
     *
     * @return The count of available routes
     */
    int getRouteCount();
}

