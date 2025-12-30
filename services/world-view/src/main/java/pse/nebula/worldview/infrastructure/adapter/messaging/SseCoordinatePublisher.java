package pse.nebula.worldview.infrastructure.adapter.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pse.nebula.worldview.domain.model.Coordinate;
import pse.nebula.worldview.domain.model.JourneyState;
import pse.nebula.worldview.domain.port.outbound.CoordinatePublisher;
import pse.nebula.worldview.infrastructure.adapter.web.dto.CoordinateUpdateDto;
import pse.nebula.worldview.infrastructure.adapter.web.mapper.DtoMapper;
import pse.nebula.worldview.infrastructure.adapter.web.sse.SseEmitterManager;

/**
 * Adapter that implements the CoordinatePublisher port.
 * Publishes coordinate updates via SSE to connected frontend clients.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SseCoordinatePublisher implements CoordinatePublisher {

    private final SseEmitterManager sseEmitterManager;
    private final DtoMapper dtoMapper;

    @Override
    public void publishCoordinateUpdate(String journeyId, Coordinate coordinate, JourneyState journeyState) {
        CoordinateUpdateDto update = dtoMapper.toCoordinateUpdate(journeyState);
        sseEmitterManager.sendUpdate(journeyId, update);

        log.debug("SSE: Sent coordinate update for journey: {} - Waypoint {}/{}", 
            journeyId, 
            journeyState.getCurrentWaypointIndex() + 1,
            journeyState.getRoute().waypoints().size());
    }

    @Override
    public void publishJourneyStarted(JourneyState journeyState) {
        CoordinateUpdateDto update = dtoMapper.toCoordinateUpdate(journeyState);
        sseEmitterManager.sendJourneyStarted(journeyState.getJourneyId(), update);

        log.info("Published journey started event for: {}", journeyState.getJourneyId());
    }

    @Override
    public void publishJourneyCompleted(JourneyState journeyState) {
        CoordinateUpdateDto update = dtoMapper.toCoordinateUpdate(journeyState);
        sseEmitterManager.sendJourneyCompleted(journeyState.getJourneyId(), update);

        log.info("Published journey completed event for: {}", journeyState.getJourneyId());
    }
}

