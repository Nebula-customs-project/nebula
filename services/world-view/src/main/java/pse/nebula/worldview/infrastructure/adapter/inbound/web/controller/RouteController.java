package pse.nebula.worldview.infrastructure.adapter.inbound.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
 * Provides read-only endpoints to query available routes.
 *
 * Routes are automatically selected by the system for journeys.
 * Users can view available routes but cannot manually select them.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/routes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Available Routes", description = "View available driving routes (read-only)")
public class RouteController {

    private final RouteUseCase routeUseCase;
    private final DtoMapper dtoMapper;

    @Operation(summary = "Get all available routes",
            description = "Returns all available driving routes. Routes are automatically selected by the system for journeys.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all routes",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RouteDto.class)))
    })
    @GetMapping
    public ResponseEntity<List<RouteDto>> getAllRoutes() {
        log.info("Fetching all available routes");

        List<DrivingRoute> routes = routeUseCase.getAllRoutes();
        List<RouteDto> routeDtos = routes.stream()
            .map(dtoMapper::toDto)
            .toList();

        return ResponseEntity.ok(routeDtos);
    }

    @Operation(summary = "Get route by ID", description = "Returns details of a specific route")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Route found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RouteDto.class))),
            @ApiResponse(responseCode = "404", description = "Route not found", content = @Content)
    })
    @GetMapping("/{routeId}")
    public ResponseEntity<RouteDto> getRouteById(
            @Parameter(description = "Unique route identifier", example = "route-1")
            @PathVariable String routeId) {
        log.info("Fetching route with ID: {}", routeId);

        DrivingRoute route = routeUseCase.getRouteById(routeId);
        return ResponseEntity.ok(dtoMapper.toDto(route));
    }

    @Operation(summary = "Get route count", description = "Returns the total number of available routes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Route count retrieved",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Integer.class)))
    })
    @GetMapping("/count")
    public ResponseEntity<Integer> getRouteCount() {
        return ResponseEntity.ok(routeUseCase.getRouteCount());
    }
}