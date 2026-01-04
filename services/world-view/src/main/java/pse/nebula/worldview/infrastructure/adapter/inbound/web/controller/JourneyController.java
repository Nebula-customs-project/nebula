package pse.nebula.worldview.infrastructure.adapter.inbound.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pse.nebula.worldview.application.service.JourneySchedulerService;
import pse.nebula.worldview.domain.model.JourneyState;
import pse.nebula.worldview.domain.port.inbound.JourneyUseCase;
import pse.nebula.worldview.infrastructure.adapter.inbound.web.dto.JourneyStateDto;
import pse.nebula.worldview.infrastructure.adapter.inbound.web.dto.StartJourneyRequest;
import pse.nebula.worldview.infrastructure.adapter.inbound.web.mapper.DtoMapper;
import pse.nebula.worldview.infrastructure.adapter.inbound.web.sse.SseEmitterManager;

import java.util.UUID;

/**
 * REST controller for journey-related operations.
 * Provides endpoints to start, stop, and track journeys.
 * Also provides SSE endpoint for real-time coordinate streaming.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/journeys")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Allow frontend access
public class JourneyController {

    private final JourneyUseCase journeyUseCase;
    private final JourneySchedulerService journeySchedulerService;
    private final SseEmitterManager sseEmitterManager;
    private final DtoMapper dtoMapper;

    /**
     * Start a new journey.
     * If routeId is not provided, a random route will be selected.
     *
     * @param request The journey start request
     * @return The initial journey state
     */
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

    /**
     * Get the current state of a journey.
     *
     * @param journeyId The journey identifier
     * @return The current journey state
     */
    @GetMapping("/{journeyId}")
    public ResponseEntity<JourneyStateDto> getJourneyState(@PathVariable String journeyId) {
        log.info("Fetching state for journey: {}", journeyId);

        JourneyState journeyState = journeyUseCase.getJourneyState(journeyId);
        return ResponseEntity.ok(dtoMapper.toDto(journeyState));
    }

    /**
     * Pause a running journey.
     *
     * @param journeyId The journey identifier
     * @return The updated journey state
     */
    @PostMapping("/{journeyId}/pause")
    public ResponseEntity<JourneyStateDto> pauseJourney(@PathVariable String journeyId) {
        log.info("Pausing journey: {}", journeyId);

        journeyUseCase.pauseJourney(journeyId);
        JourneyState journeyState = journeyUseCase.getJourneyState(journeyId);
        return ResponseEntity.ok(dtoMapper.toDto(journeyState));
    }

    /**
     * Resume a paused journey.
     *
     * @param journeyId The journey identifier
     * @return The updated journey state
     */
    @PostMapping("/{journeyId}/resume")
    public ResponseEntity<JourneyStateDto> resumeJourney(@PathVariable String journeyId) {
        log.info("Resuming journey: {}", journeyId);

        journeyUseCase.resumeJourney(journeyId);
        JourneyState journeyState = journeyUseCase.getJourneyState(journeyId);
        return ResponseEntity.ok(dtoMapper.toDto(journeyState));
    }

    /**
     * Stop and remove a journey.
     *
     * @param journeyId The journey identifier
     * @return No content
     */
    @DeleteMapping("/{journeyId}")
    public ResponseEntity<Void> stopJourney(@PathVariable String journeyId) {
        log.info("Stopping journey: {}", journeyId);

        journeySchedulerService.unregisterJourney(journeyId);
        journeyUseCase.stopJourney(journeyId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Subscribe to real-time coordinate updates for a journey via Server-Sent Events.
     * The frontend should call this endpoint to receive coordinate updates as the car moves.
     *
     * @param journeyId The journey identifier
     * @return SSE stream of coordinate updates
     */
    @GetMapping(value = "/{journeyId}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamCoordinates(@PathVariable String journeyId) {
        String clientId = UUID.randomUUID().toString();
        log.info("New SSE subscription for journey: {}, client: {}", journeyId, clientId);

        return sseEmitterManager.createEmitter(journeyId, clientId);
    }

    /**
     * Quick start a journey with random route and default speed.
     * Convenience endpoint for frontend that generates a new journey ID.
     *
     * @return The initial journey state with a generated ID
     */
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

