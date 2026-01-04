package pse.nebula.worldview.infrastructure.adapter.outbound.persistence.repository;

import pse.nebula.worldview.domain.model.Coordinate;
import pse.nebula.worldview.domain.model.DrivingRoute;
import pse.nebula.worldview.domain.port.outbound.RouteRepository;

import jakarta.annotation.PostConstruct;
import java.util.*;

/**
 * In-memory implementation of RouteRepository containing 8 predefined routes to Porsche Zentrum Stuttgart.
 * This is kept as a fallback/testing implementation.
 * The primary implementation is JpaRouteRepositoryAdapter.
 *
 * This adapter implements the outbound port for route persistence,
 * following the Hexagonal Architecture pattern.
 *
 * All routes end at the dealership: Porsche Zentrum Stuttgart
 * Address: Porschestraße 1, 70435 Stuttgart, Germany
 * Coordinates: 48.8354, 9.1520
 */
public class InMemoryRouteRepositoryAdapter implements RouteRepository {

    // Porsche Zentrum Stuttgart - Destination for all routes
    private static final Coordinate DEALERSHIP = new Coordinate(48.8354, 9.1520);

    private final Map<String, DrivingRoute> routes = new LinkedHashMap<>();

    @PostConstruct
    public void initializeRoutes() {
        // Route 1: From Ludwigsburg Schloss
        createRoute1FromLudwigsburg();

        // Route 2: From Favoritepark (Ludwigsburg)
        createRoute2FromFavoritepark();

        // Route 3: From Esslingen am Neckar
        createRoute3FromEsslingen();

        // Route 4: From Böblingen
        createRoute4FromBoblingen();

        // Route 5: From Sindelfingen
        createRoute5FromSindelfingen();

        // Route 6: From Waiblingen
        createRoute6FromWaiblingen();

        // Route 7: From Fellbach
        createRoute7FromFellbach();

        // Route 8: From Kornwestheim
        createRoute8FromKornwestheim();
    }

    /**
     * Route 1: Ludwigsburg Schloss → Porsche Zentrum Stuttgart
     * Distance: ~15 km, Duration: ~20 min
     * Via B27 and Stammheimer Straße
     */
    private void createRoute1FromLudwigsburg() {
        List<Coordinate> waypoints = Arrays.asList(
            new Coordinate(48.8973, 9.1920),  // Ludwigsburg Schloss
            new Coordinate(48.8945, 9.1875),  // Schlossstraße
            new Coordinate(48.8912, 9.1832),  // Myliusstraße
            new Coordinate(48.8878, 9.1798),  // Friedrichstraße
            new Coordinate(48.8842, 9.1765),  // B27 entry
            new Coordinate(48.8798, 9.1721),  // B27
            new Coordinate(48.8754, 9.1678),  // B27
            new Coordinate(48.8712, 9.1642),  // B27
            new Coordinate(48.8668, 9.1605),  // B27
            new Coordinate(48.8625, 9.1568),  // B27
            new Coordinate(48.8582, 9.1534),  // Stammheimer Straße
            new Coordinate(48.8538, 9.1498),  // Stammheimer Straße
            new Coordinate(48.8495, 9.1462),  // Approaching Zuffenhausen
            new Coordinate(48.8452, 9.1478),  // Zuffenhausen
            new Coordinate(48.8408, 9.1495),  // Porschestraße
            new Coordinate(48.8382, 9.1508),  // Final approach
            DEALERSHIP
        );

        addRoute("route-1", "Ludwigsburg Schloss Route",
            "From Ludwigsburg Palace via B27 - Scenic castle start",
            waypoints, 15000, 1200);
    }

    /**
     * Route 2: Favoritepark (Ludwigsburg) → Porsche Zentrum Stuttgart
     * Distance: ~12 km, Duration: ~18 min
     * Via Favoritepark and Stammheimer Straße
     */
    private void createRoute2FromFavoritepark() {
        List<Coordinate> waypoints = Arrays.asList(
            new Coordinate(48.8821, 9.1678),  // Favoritepark
            new Coordinate(48.8792, 9.1654),  // Park exit
            new Coordinate(48.8758, 9.1632),  // Favorite-Allee
            new Coordinate(48.8725, 9.1608),  // Heading south
            new Coordinate(48.8692, 9.1585),  // Approaching Kornwestheim
            new Coordinate(48.8658, 9.1562),  // Kornwestheim area
            new Coordinate(48.8625, 9.1542),  // L1140
            new Coordinate(48.8592, 9.1525),  // L1140
            new Coordinate(48.8558, 9.1508),  // Stammheimer Straße
            new Coordinate(48.8525, 9.1492),  // Stammheimer Straße
            new Coordinate(48.8492, 9.1478),  // Approaching Zuffenhausen
            new Coordinate(48.8458, 9.1485),  // Zuffenhausen
            new Coordinate(48.8418, 9.1498),  // Porschestraße entry
            new Coordinate(48.8385, 9.1510),  // Final stretch
            DEALERSHIP
        );

        addRoute("route-2", "Favoritepark Route",
            "From Favoritepark Ludwigsburg via scenic park roads",
            waypoints, 12000, 1080);
    }

    /**
     * Route 3: Esslingen am Neckar → Porsche Zentrum Stuttgart
     * Distance: ~20 km, Duration: ~25 min
     * Via B10 and Neckartalstraße
     */
    private void createRoute3FromEsslingen() {
        List<Coordinate> waypoints = Arrays.asList(
            new Coordinate(48.7395, 9.3108),  // Esslingen Marktplatz
            new Coordinate(48.7428, 9.2985),  // Neckarstraße
            new Coordinate(48.7458, 9.2862),  // B10 entry
            new Coordinate(48.7492, 9.2738),  // B10
            new Coordinate(48.7528, 9.2615),  // B10
            new Coordinate(48.7568, 9.2492),  // B10 Untertürkheim
            new Coordinate(48.7612, 9.2368),  // Bad Cannstatt approach
            new Coordinate(48.7658, 9.2245),  // Bad Cannstatt
            new Coordinate(48.7708, 9.2122),  // Neckartalstraße
            new Coordinate(48.7762, 9.1998),  // Heading north
            new Coordinate(48.7818, 9.1875),  // B10/B14 junction
            new Coordinate(48.7878, 9.1785),  // Stuttgart Mitte
            new Coordinate(48.7942, 9.1695),  // Heading north
            new Coordinate(48.8008, 9.1625),  // Feuerbach
            new Coordinate(48.8078, 9.1575),  // Feuerbach
            new Coordinate(48.8148, 9.1538),  // Approaching Zuffenhausen
            new Coordinate(48.8218, 9.1512),  // Zuffenhausen
            new Coordinate(48.8288, 9.1502),  // Porschestraße
            DEALERSHIP
        );

        addRoute("route-3", "Esslingen Route",
            "From Esslingen historic center via B10 along the Neckar",
            waypoints, 20000, 1500);
    }

    /**
     * Route 4: Böblingen → Porsche Zentrum Stuttgart
     * Distance: ~22 km, Duration: ~28 min
     * Via A81 and B295
     */
    private void createRoute4FromBoblingen() {
        List<Coordinate> waypoints = Arrays.asList(
            new Coordinate(48.6862, 9.0145),  // Böblingen Stadtmitte
            new Coordinate(48.6912, 9.0198),  // Heading to A81
            new Coordinate(48.6968, 9.0252),  // A81 entry
            new Coordinate(48.7028, 9.0308),  // A81
            new Coordinate(48.7092, 9.0365),  // A81
            new Coordinate(48.7158, 9.0422),  // A81
            new Coordinate(48.7228, 9.0478),  // A81
            new Coordinate(48.7298, 9.0535),  // A81
            new Coordinate(48.7372, 9.0592),  // A81 Vaihingen
            new Coordinate(48.7448, 9.0685),  // B295 entry
            new Coordinate(48.7528, 9.0778),  // B295
            new Coordinate(48.7608, 9.0872),  // B295
            new Coordinate(48.7692, 9.0965),  // Stuttgart Vaihingen
            new Coordinate(48.7778, 9.1058),  // Heading north
            new Coordinate(48.7868, 9.1152),  // Stuttgart Mitte
            new Coordinate(48.7958, 9.1245),  // Feuerbach direction
            new Coordinate(48.8052, 9.1338),  // Feuerbach
            new Coordinate(48.8148, 9.1432),  // Zuffenhausen approach
            new Coordinate(48.8248, 9.1478),  // Zuffenhausen
            DEALERSHIP
        );

        addRoute("route-4", "Böblingen Route",
            "From Böblingen via A81 Autobahn - Fast highway route",
            waypoints, 22000, 1680);
    }

    /**
     * Route 5: Sindelfingen → Porsche Zentrum Stuttgart
     * Distance: ~18 km, Duration: ~24 min
     * Via Mahdentalstraße and B14
     */
    private void createRoute5FromSindelfingen() {
        List<Coordinate> waypoints = Arrays.asList(
            new Coordinate(48.7132, 9.0028),  // Sindelfingen Zentrum (Mercedes HQ area)
            new Coordinate(48.7178, 9.0108),  // Heading east
            new Coordinate(48.7225, 9.0188),  // Industrial area
            new Coordinate(48.7272, 9.0268),  // Mahdentalstraße
            new Coordinate(48.7322, 9.0348),  // Vaihingen approach
            new Coordinate(48.7372, 9.0428),  // Vaihingen
            new Coordinate(48.7425, 9.0508),  // B14 entry
            new Coordinate(48.7478, 9.0588),  // B14
            new Coordinate(48.7535, 9.0668),  // B14
            new Coordinate(48.7592, 9.0748),  // B14
            new Coordinate(48.7652, 9.0828),  // B14 Stuttgart
            new Coordinate(48.7712, 9.0908),  // Stuttgart West
            new Coordinate(48.7778, 9.1018),  // City center
            new Coordinate(48.7848, 9.1128),  // Heading north
            new Coordinate(48.7918, 9.1238),  // Feuerbach
            new Coordinate(48.7992, 9.1348),  // Feuerbach
            new Coordinate(48.8068, 9.1432),  // Zuffenhausen approach
            new Coordinate(48.8148, 9.1478),  // Zuffenhausen
            new Coordinate(48.8248, 9.1498),  // Porschestraße
            DEALERSHIP
        );

        addRoute("route-5", "Sindelfingen Route",
            "From Sindelfingen (Mercedes-Benz city) to Porsche - Rival territory route",
            waypoints, 18000, 1440);
    }

    /**
     * Route 6: Waiblingen → Porsche Zentrum Stuttgart
     * Distance: ~16 km, Duration: ~22 min
     * Via B14 and B29
     */
    private void createRoute6FromWaiblingen() {
        List<Coordinate> waypoints = Arrays.asList(
            new Coordinate(48.8312, 9.3168),  // Waiblingen Altstadt
            new Coordinate(48.8285, 9.2998),  // Heading west
            new Coordinate(48.8258, 9.2828),  // B29 entry
            new Coordinate(48.8232, 9.2658),  // B29
            new Coordinate(48.8205, 9.2488),  // B29
            new Coordinate(48.8178, 9.2318),  // B29
            new Coordinate(48.8152, 9.2148),  // B29 / Bad Cannstatt approach
            new Coordinate(48.8125, 9.1978),  // Bad Cannstatt
            new Coordinate(48.8098, 9.1808),  // Stuttgart Nord
            new Coordinate(48.8145, 9.1695),  // Heading to Zuffenhausen
            new Coordinate(48.8195, 9.1625),  // Approaching Zuffenhausen
            new Coordinate(48.8248, 9.1565),  // Zuffenhausen
            new Coordinate(48.8302, 9.1535),  // Porschestraße entry
            DEALERSHIP
        );

        addRoute("route-6", "Waiblingen Route",
            "From Waiblingen old town via B29 through Rems Valley",
            waypoints, 16000, 1320);
    }

    /**
     * Route 7: Fellbach → Porsche Zentrum Stuttgart
     * Distance: ~10 km, Duration: ~15 min
     * Via Fellbacher Straße and Schmidener Straße
     */
    private void createRoute7FromFellbach() {
        List<Coordinate> waypoints = Arrays.asList(
            new Coordinate(48.8108, 9.2762),  // Fellbach Zentrum
            new Coordinate(48.8098, 9.2618),  // Fellbacher Straße
            new Coordinate(48.8088, 9.2475),  // Heading west
            new Coordinate(48.8078, 9.2332),  // Bad Cannstatt approach
            new Coordinate(48.8068, 9.2188),  // Bad Cannstatt
            new Coordinate(48.8082, 9.2045),  // Schmidener Straße
            new Coordinate(48.8098, 9.1902),  // Stuttgart Nord
            new Coordinate(48.8118, 9.1758),  // Heading to Zuffenhausen
            new Coordinate(48.8158, 9.1658),  // Approaching Zuffenhausen
            new Coordinate(48.8208, 9.1588),  // Zuffenhausen
            new Coordinate(48.8268, 9.1545),  // Porschestraße
            new Coordinate(48.8318, 9.1528),  // Final approach
            DEALERSHIP
        );

        addRoute("route-7", "Fellbach Route",
            "From Fellbach wine town - Short scenic route",
            waypoints, 10000, 900);
    }

    /**
     * Route 8: Kornwestheim → Porsche Zentrum Stuttgart
     * Distance: ~5 km, Duration: ~10 min
     * Via Stammheimer Straße - Closest route
     */
    private void createRoute8FromKornwestheim() {
        List<Coordinate> waypoints = Arrays.asList(
            new Coordinate(48.8598, 9.1858),  // Kornwestheim Bahnhof
            new Coordinate(48.8572, 9.1795),  // Stuttgarter Straße
            new Coordinate(48.8545, 9.1732),  // Heading south
            new Coordinate(48.8518, 9.1668),  // Stammheimer Straße entry
            new Coordinate(48.8492, 9.1605),  // Stammheimer Straße
            new Coordinate(48.8465, 9.1558),  // Approaching Zuffenhausen
            new Coordinate(48.8438, 9.1535),  // Zuffenhausen
            new Coordinate(48.8408, 9.1522),  // Porschestraße entry
            new Coordinate(48.8378, 9.1515),  // Porschestraße
            DEALERSHIP
        );

        addRoute("route-8", "Kornwestheim Route",
            "From Kornwestheim - Quick direct route",
            waypoints, 5000, 600);
    }

    private void addRoute(String id, String name, String description,
                          List<Coordinate> waypoints, double distanceMeters, int durationSeconds) {
        DrivingRoute route = new DrivingRoute(
            id, name, description, waypoints, distanceMeters, durationSeconds
        );

        routes.put(id, route);
    }

    @Override
    public List<DrivingRoute> findAll() {
        return new ArrayList<>(routes.values());
    }

    @Override
    public Optional<DrivingRoute> findById(String routeId) {
        return Optional.ofNullable(routes.get(routeId));
    }

    @Override
    public int count() {
        return routes.size();
    }
}