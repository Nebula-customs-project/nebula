package pse.nebula.worldview.domain.port.outbound;

import pse.nebula.worldview.domain.model.DrivingRoute;

import java.util.List;
import java.util.Optional;

/**
 * Outbound port for accessing route data.
 * This is a secondary port implemented by infrastructure adapters.
 */
public interface RouteRepository {

    /**
     * Find all available routes.
     *
     * @return List of all driving routes
     */
    List<DrivingRoute> findAll();

    /**
     * Find a route by its ID.
     *
     * @param routeId The route identifier
     * @return Optional containing the route if found
     */
    Optional<DrivingRoute> findById(String routeId);

    /**
     * Get the total count of available routes.
     *
     * @return The number of routes
     */
    int count();
}

