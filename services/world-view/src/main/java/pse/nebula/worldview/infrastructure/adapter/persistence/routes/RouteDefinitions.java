package pse.nebula.worldview.infrastructure.adapter.persistence.routes;

import pse.nebula.worldview.domain.model.Coordinate;

import java.util.List;

/**
 * Defines all driving routes to Porsche Zentrum Stuttgart.
 * Each route specifies a starting location and metadata.
 * Actual road-following waypoints are fetched from OSRM routing API.
 */
public final class RouteDefinitions {

    private RouteDefinitions() {
        // Utility class - prevent instantiation
    }

    /**
     * Porsche Zentrum Stuttgart - Destination for all routes
     * Address: Porschestraße 1, 70435 Stuttgart, Germany
     */
    public static final Coordinate PORSCHE_ZENTRUM_STUTTGART = new Coordinate(48.8354, 9.1520);

    /**
     * Returns all predefined route definitions.
     * Routes are ordered by proximity and scenic value.
     */
    public static List<RouteDefinition> getAllRoutes() {
        return List.of(
            // Route 1: Ludwigsburg Schloss
            // Starting at the famous Ludwigsburg Palace
            RouteDefinition.toDealer(
                "route-1",
                "Ludwigsburg Schloss Route",
                "From Ludwigsburg Palace via B27 - Scenic castle start",
                new Coordinate(48.8973, 9.1920),  // Ludwigsburg Schloss
                15000,  // ~15 km
                1200    // ~20 min
            ),

            // Route 2: Favoritepark
            // Starting at SWLB Mobilität, Reuteallee (near Favoritepark)
            RouteDefinition.toDealer(
                "route-2",
                "Favoritepark Route",
                "From SWLB Mobilität via B27 to Porsche Zentrum",
                new Coordinate(48.9080, 9.1797),  // SWLB Mobilität, Reuteallee
                14300,  // ~14.3 km
                800     // ~13 min
            ),

            // Route 3: Kornwestheim
            // Starting at Bahnhofstraße - closest route to dealership
            RouteDefinition.toDealer(
                "route-3",
                "Kornwestheim Route",
                "From Kornwestheim via Stammheimer Straße - Quick route",
                new Coordinate(48.8598, 9.1858),  // Kornwestheim Bahnhofstraße
                5000,   // ~5 km
                600     // ~10 min
            ),

            // Route 4: Böblingen
            // Starting from Böblingen city center
            RouteDefinition.toDealer(
                "route-4",
                "Böblingen Route",
                "From Böblingen via A81 - Southern approach",
                new Coordinate(48.6863, 9.0146),  // Böblingen Bahnhof
                25000,  // ~25 km
                1500    // ~25 min
            ),

            // Route 5: Reutlingen
            // Starting from Alteburgstraße 150
            RouteDefinition.toDealer(
                "route-5",
                "Reutlingen Route",
                "From Alteburgstraße 150, Reutlingen - Long scenic drive",
                new Coordinate(48.4826, 9.2048),  // Alteburgstraße 150, Reutlingen
                45000,  // ~45 km
                2400    // ~40 min
            ),

            // Route 6: Waiblingen
            // Starting from Waiblingen city center
            RouteDefinition.toDealer(
                "route-6",
                "Waiblingen Route",
                "From Waiblingen via B14 - Eastern approach",
                new Coordinate(48.8305, 9.3167),  // Waiblingen Stadtmitte
                18000,  // ~18 km
                1200    // ~20 min
            ),

            // Route 7: Fellbach
            // Starting from Fellbach city center
            RouteDefinition.toDealer(
                "route-7",
                "Fellbach Route",
                "From Fellbach via B14 - Wine country start",
                new Coordinate(48.8089, 9.2757),  // Fellbach Stadtmitte
                15000,  // ~15 km
                1020    // ~17 min
            ),

            // Route 8: Esslingen
            // Starting from Esslingen am Neckar
            RouteDefinition.toDealer(
                "route-8",
                "Esslingen Route",
                "From Esslingen via B10 - Historic town start",
                new Coordinate(48.7406, 9.3048),  // Esslingen Bahnhof
                22000,  // ~22 km
                1500    // ~25 min
            )
        );
    }
}
