package pse.nebula.worldview.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pse.nebula.worldview.domain.exception.RouteNotFoundException;
import pse.nebula.worldview.domain.model.DrivingRoute;
import pse.nebula.worldview.domain.port.inbound.RouteUseCase;
import pse.nebula.worldview.domain.port.outbound.RouteRepository;

import java.util.List;
import java.util.Random;

/**
 * Application service that implements route-related use cases.
 * Acts as a facade between the inbound ports and the domain.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RouteService implements RouteUseCase {

    private final RouteRepository routeRepository;
    private final Random random = new Random();

    @Override
    public List<DrivingRoute> getAllRoutes() {
        log.debug("Fetching all available routes");
        return routeRepository.findAll();
    }

    @Override
    public DrivingRoute getRouteById(String routeId) {
        log.debug("Fetching route with ID: {}", routeId);
        return routeRepository.findById(routeId)
            .orElseThrow(() -> new RouteNotFoundException(routeId));
    }

    @Override
    public DrivingRoute getRandomRoute() {
        log.debug("Selecting a random route");
        List<DrivingRoute> routes = routeRepository.findAll();

        if (routes.isEmpty()) {
            throw RouteNotFoundException.noRoutesAvailable();
        }

        int randomIndex = random.nextInt(routes.size());
        DrivingRoute selectedRoute = routes.get(randomIndex);

        log.info("Selected random route: {} ({})", selectedRoute.name(), selectedRoute.id());
        return selectedRoute;
    }

    @Override
    public int getRouteCount() {
        return routeRepository.count();
    }
}

