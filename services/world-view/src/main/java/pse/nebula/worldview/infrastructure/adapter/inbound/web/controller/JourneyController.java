package pse.nebula.worldview.infrastructure.adapter.inbound.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pse.nebula.worldview.application.service.JourneySchedulerService;
import pse.nebula.worldview.domain.model.JourneyState;
import pse.nebula.worldview.domain.port.inbound.JourneyUseCase;
import pse.nebula.worldview.infrastructure.adapter.inbound.web.dto.JourneyStateDto;
import pse.nebula.worldview.infrastructure.adapter.inbound.web.dto.StartJourneyRequest;
import pse.nebula.worldview.infrastructure.adapter.inbound.web.mapper.DtoMapper;

import java.util.UUID;

/**
 * REST controller for journey-related operations.
 * Provides endpoints to start, stop, and track journeys.
 *
 * Real-time coordinate streaming is handled via MQTT (RabbitMQ).
 * Frontend subscribes to MQTT topics: nebula/journey/{journeyId}/position
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/journeys")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Journeys", description = "Endpoints for journey lifecycle management")
public class JourneyController {

    private final JourneyUseCase journeyUseCase;
    private final JourneySchedulerService journeySchedulerService;
    private final DtoMapper dtoMapper;

    @Operation(summary = "Start a new journey",
            description = "Starts a journey on a specified route or random route. Subscribe to MQTT topic 'nebula/journey/{journeyId}/position' for real-time updates.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Journey started successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = JourneyStateDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Route not found", content = @Content)
    })
    @PostMapping
    public ResponseEntity<JourneyStateDto> startJourney(@Valid @RequestBody StartJourneyRequest request) {
        log.info("Starting new journey with ID: {}", request.getJourneyId());

        JourneyState journeyState;

        if (request.getRouteId() != null && !request.getRouteId().isBlank()) {
            journeyState = journeyUseCase.startJourneyOnRoute(
                request.getJourneyId(),
                request.getRouteId(),
                request.getSpeedMetersPerSecond()
            );
        } else {
            journeyState = journeyUseCase.startNewJourney(
                request.getJourneyId(),
                request.getSpeedMetersPerSecond()
            );
        }

        // Register journey for scheduled updates
        log.info("About to register journey: {} for scheduled updates", request.getJourneyId());
        journeySchedulerService.registerJourney(request.getJourneyId());
        log.info("Journey registered successfully, returning response");

        return ResponseEntity.status(HttpStatus.CREATED).body(dtoMapper.toDto(journeyState));
    }

    @Operation(summary = "Get journey state", description = "Returns the current state of a journey")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Journey state retrieved",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = JourneyStateDto.class))),
            @ApiResponse(responseCode = "404", description = "Journey not found", content = @Content)
    })
    @GetMapping("/{journeyId}")
    public ResponseEntity<JourneyStateDto> getJourneyState(
            @Parameter(description = "Unique journey identifier", example = "journey-1234567890")
            @PathVariable String journeyId) {
        log.info("Fetching state for journey: {}", journeyId);

        JourneyState journeyState = journeyUseCase.getJourneyState(journeyId);
        return ResponseEntity.ok(dtoMapper.toDto(journeyState));
    }

    @Operation(summary = "Pause journey", description = "Pauses a running journey")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Journey paused",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = JourneyStateDto.class))),
            @ApiResponse(responseCode = "404", description = "Journey not found", content = @Content)
    })
    @PostMapping("/{journeyId}/pause")
    public ResponseEntity<JourneyStateDto> pauseJourney(
            @Parameter(description = "Unique journey identifier")
            @PathVariable String journeyId) {
        log.info("Pausing journey: {}", journeyId);

        journeyUseCase.pauseJourney(journeyId);
        JourneyState journeyState = journeyUseCase.getJourneyState(journeyId);
        return ResponseEntity.ok(dtoMapper.toDto(journeyState));
    }

    @Operation(summary = "Resume journey", description = "Resumes a paused journey")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Journey resumed",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = JourneyStateDto.class))),
            @ApiResponse(responseCode = "404", description = "Journey not found", content = @Content)
    })
    @PostMapping("/{journeyId}/resume")
    public ResponseEntity<JourneyStateDto> resumeJourney(
            @Parameter(description = "Unique journey identifier")
            @PathVariable String journeyId) {
        log.info("Resuming journey: {}", journeyId);

        journeyUseCase.resumeJourney(journeyId);
        JourneyState journeyState = journeyUseCase.getJourneyState(journeyId);
        return ResponseEntity.ok(dtoMapper.toDto(journeyState));
    }

    @Operation(summary = "Stop journey", description = "Stops and removes a journey")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Journey stopped successfully"),
            @ApiResponse(responseCode = "404", description = "Journey not found", content = @Content)
    })
    @DeleteMapping("/{journeyId}")
    public ResponseEntity<Void> stopJourney(
            @Parameter(description = "Unique journey identifier")
            @PathVariable String journeyId) {
        log.info("Stopping journey: {}", journeyId);

        journeySchedulerService.unregisterJourney(journeyId);
        journeyUseCase.stopJourney(journeyId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Quick start journey",
            description = "Starts a journey with auto-generated ID, random route, and default speed (15 m/s ≈ 54 km/h)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Journey started successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = JourneyStateDto.class)))
    })
    @PostMapping("/quick-start")
    public ResponseEntity<JourneyStateDto> quickStartJourney() {
        String journeyId = "journey-" + UUID.randomUUID().toString().substring(0, 8);
        double defaultSpeed = 15.0; // 15 m/s ≈ 54 km/h

        log.info("Quick starting journey with generated ID: {}", journeyId);

        JourneyState journeyState = journeyUseCase.startNewJourney(journeyId, defaultSpeed);
        journeySchedulerService.registerJourney(journeyId);

        return ResponseEntity.status(HttpStatus.CREATED).body(dtoMapper.toDto(journeyState));
    }
}