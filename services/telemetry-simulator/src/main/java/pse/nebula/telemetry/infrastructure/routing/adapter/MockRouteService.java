package pse.nebula.telemetry.infrastructure.routing.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pse.nebula.telemetry.domain.GeoPoint;
import pse.nebula.telemetry.domain.Route;
import pse.nebula.telemetry.domain.RouteServicePort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of RouteServicePort for testing purposes.
 * Returns a straight line route with intermediate waypoints between start and end.
 *
 * In production, this would be replaced with a real routing service
 * (e.g., Google Maps API, OpenStreetMap, etc.)
 */
@Slf4j
@Component
public class MockRouteService implements RouteServicePort {

    private static final int DEFAULT_WAYPOINTS = 10;

    @Override
    public Route getRoute(GeoPoint start, GeoPoint end) {
        log.debug("Generating mock route from {} to {}", start, end);

        if (start == null || end == null) {
            throw new IllegalArgumentException("Start and end points cannot be null");
        }

        List<GeoPoint> waypoints = generateStraightLineRoute(start, end, DEFAULT_WAYPOINTS);

        log.debug("Generated route with {} waypoints", waypoints.size());
        return new Route(waypoints);
    }

    /**
     * Generates a straight line route with intermediate waypoints.
     *
     * @param start Starting point
     * @param end Ending point
     * @param numIntermediatePoints Number of intermediate points to generate
     * @return List of waypoints including start and end
     */
    private List<GeoPoint> generateStraightLineRoute(GeoPoint start, GeoPoint end, int numIntermediatePoints) {
        List<GeoPoint> waypoints = new ArrayList<>();
        waypoints.add(start);

        // Calculate the delta for each step
        double latStep = (end.lat() - start.lat()) / (numIntermediatePoints + 1);
        double lngStep = (end.lng() - start.lng()) / (numIntermediatePoints + 1);

        // Generate intermediate waypoints
        for (int i = 1; i <= numIntermediatePoints; i++) {
            double lat = start.lat() + (latStep * i);
            double lng = start.lng() + (lngStep * i);
            waypoints.add(new GeoPoint(lat, lng));
        }

        waypoints.add(end);
        return waypoints;
    }
}

