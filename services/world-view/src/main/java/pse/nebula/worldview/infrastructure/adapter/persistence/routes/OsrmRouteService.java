package pse.nebula.worldview.infrastructure.adapter.persistence.routes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pse.nebula.worldview.domain.model.Coordinate;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Service that fetches road-following waypoints from OSRM (Open Source Routing Machine).
 * OSRM provides real driving routes that follow actual roads.
 * 
 * API: https://router.project-osrm.org
 */
@Slf4j
@Service
public class OsrmRouteService {

    private static final String OSRM_BASE_URL = "http://router.project-osrm.org/route/v1/driving";
    private static final int MAX_WAYPOINTS = 100;  // Maximum waypoints per route

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public OsrmRouteService() {
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Fetches road-following waypoints from OSRM for a given route definition.
     * 
     * @param routeDefinition The route definition containing start and end points
     * @return List of coordinates following actual roads
     */
    public List<Coordinate> fetchWaypoints(RouteDefinition routeDefinition) {
        return fetchWaypoints(routeDefinition.startPoint(), routeDefinition.endPoint());
    }

    /**
     * Fetches road-following waypoints from OSRM for given start and end coordinates.
     * The returned waypoints follow actual roads, not straight lines.
     * 
     * @param start Starting coordinate
     * @param end   Ending coordinate
     * @return List of coordinates following actual roads, sampled to reduce count
     */
    public List<Coordinate> fetchWaypoints(Coordinate start, Coordinate end) {
        try {
            // OSRM uses longitude,latitude format - use Locale.US to ensure dot decimal separator
            String url = String.format(java.util.Locale.US, "%s/%.6f,%.6f;%.6f,%.6f?overview=full&geometries=geojson",
                OSRM_BASE_URL,
                start.longitude(), start.latitude(),
                end.longitude(), end.latitude()
            );

            log.info("Fetching route from OSRM URL: {}", url);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .timeout(Duration.ofSeconds(30))
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log.warn("OSRM returned status {}: {}", response.statusCode(), response.body());
                return createFallbackWaypoints(start, end);
            }

            return parseOsrmResponse(response.body(), start, end);

        } catch (Exception e) {
            log.error("Failed to fetch route from OSRM: {}", e.getMessage());
            return createFallbackWaypoints(start, end);
        }
    }

    /**
     * Parses the OSRM JSON response and extracts waypoint coordinates.
     */
    private List<Coordinate> parseOsrmResponse(String jsonResponse, Coordinate start, Coordinate end) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode routes = root.get("routes");

            if (routes == null || routes.isEmpty()) {
                log.warn("No routes found in OSRM response");
                return createFallbackWaypoints(start, end);
            }

            JsonNode geometry = routes.get(0).get("geometry");
            JsonNode coordinates = geometry.get("coordinates");

            if (coordinates == null || coordinates.isEmpty()) {
                log.warn("No coordinates found in OSRM response");
                return createFallbackWaypoints(start, end);
            }

            List<Coordinate> allCoordinates = new ArrayList<>();
            for (JsonNode coord : coordinates) {
                // OSRM returns [longitude, latitude]
                double lon = coord.get(0).asDouble();
                double lat = coord.get(1).asDouble();
                allCoordinates.add(new Coordinate(lat, lon));
            }

            // Sample coordinates to reduce count while maintaining route shape
            List<Coordinate> sampledCoordinates = sampleCoordinates(allCoordinates);
            
            log.info("Fetched {} waypoints from OSRM (sampled from {})", 
                sampledCoordinates.size(), allCoordinates.size());

            return sampledCoordinates;

        } catch (Exception e) {
            log.error("Failed to parse OSRM response: {}", e.getMessage());
            return createFallbackWaypoints(start, end);
        }
    }

    /**
     * Samples coordinates to reduce the total count while preserving the route shape.
     * Always includes the first and last coordinates.
     */
    private List<Coordinate> sampleCoordinates(List<Coordinate> coordinates) {
        if (coordinates.size() <= MAX_WAYPOINTS) {
            return coordinates;
        }

        List<Coordinate> sampled = new ArrayList<>();
        sampled.add(coordinates.get(0)); // Always include start

        // Calculate interval to get roughly MAX_WAYPOINTS
        int interval = Math.max(1, coordinates.size() / MAX_WAYPOINTS);

        for (int i = interval; i < coordinates.size() - 1; i += interval) {
            sampled.add(coordinates.get(i));
        }

        sampled.add(coordinates.get(coordinates.size() - 1)); // Always include end

        return sampled;
    }

    /**
     * Creates simple fallback waypoints if OSRM fails.
     * Uses a straight line with intermediate points.
     */
    private List<Coordinate> createFallbackWaypoints(Coordinate start, Coordinate end) {
        log.warn("Using fallback straight-line waypoints");
        
        List<Coordinate> waypoints = new ArrayList<>();
        int numPoints = 10;

        for (int i = 0; i <= numPoints; i++) {
            double ratio = (double) i / numPoints;
            double lat = start.latitude() + (end.latitude() - start.latitude()) * ratio;
            double lon = start.longitude() + (end.longitude() - start.longitude()) * ratio;
            waypoints.add(new Coordinate(lat, lon));
        }

        return waypoints;
    }
}
