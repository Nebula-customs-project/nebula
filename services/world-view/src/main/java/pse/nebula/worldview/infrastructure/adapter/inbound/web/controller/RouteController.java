package pse.nebula.worldview.infrastructure.adapter.inbound.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pse.nebula.worldview.domain.model.DrivingRoute;
import pse.nebula.worldview.domain.port.inbound.RouteUseCase;
import pse.nebula.worldview.infrastructure.adapter.inbound.web.dto.RouteDto;
import pse.nebula.worldview.infrastructure.adapter.inbound.web.mapper.DtoMapper;

import java.util.List;

/**
 * REST controller for route-related operations.
 * Provides endpoints to query available routes.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/routes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Allow frontend access
public class RouteController {

    private final RouteUseCase routeUseCase;
    private final DtoMapper dtoMapper;

    /**
     * Get all available routes to the dealership.
     *
     * @return List of all routes
     */
    @GetMapping
    public ResponseEntity<List<RouteDto>> getAllRoutes() {
        log.info("Fetching all available routes");

        List<DrivingRoute> routes = routeUseCase.getAllRoutes();
        List<RouteDto> routeDtos = routes.stream()
            .map(dtoMapper::toDto)
            .toList();

        return ResponseEntity.ok(routeDtos);
    }

    /**
     * Get a specific route by ID.
     *
     * @param routeId The route identifier
     * @return The route details
     */
    @GetMapping("/{routeId}")
    public ResponseEntity<RouteDto> getRouteById(@PathVariable String routeId) {
        log.info("Fetching route with ID: {}", routeId);

        DrivingRoute route = routeUseCase.getRouteById(routeId);
        return ResponseEntity.ok(dtoMapper.toDto(route));
    }

    /**
     * Get a random route for a new journey.
     * This is typically called on UI reload.
     *
     * @return A randomly selected route
     */
    @GetMapping("/random")
    public ResponseEntity<RouteDto> getRandomRoute() {
        log.info("Selecting a random route");

        DrivingRoute route = routeUseCase.getRandomRoute();
        return ResponseEntity.ok(dtoMapper.toDto(route));
    }

    /**
     * Get the total number of available routes.
     *
     * @return The count
     */
    @GetMapping("/count")
    public ResponseEntity<Integer> getRouteCount() {
        return ResponseEntity.ok(routeUseCase.getRouteCount());
    }
}