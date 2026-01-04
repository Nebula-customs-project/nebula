package pse.nebula.worldview.infrastructure.adapter.outbound.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pse.nebula.worldview.domain.model.Coordinate;
import pse.nebula.worldview.domain.model.JourneyState;
import pse.nebula.worldview.domain.port.outbound.CoordinatePublisher;
import pse.nebula.worldview.infrastructure.adapter.inbound.web.dto.CoordinateUpdateDto;
import pse.nebula.worldview.infrastructure.adapter.inbound.web.mapper.DtoMapper;
import pse.nebula.worldview.infrastructure.adapter.inbound.web.sse.SseEmitterManager;

/**
 * SSE-based adapter that implements the CoordinatePublisher outbound port.
 * Publishes coordinate updates via Server-Sent Events to connected frontend clients.
 *
 * This adapter follows the Hexagonal Architecture pattern, implementing
 * an outbound port to push data to external consumers (browsers).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SseCoordinatePublisherAdapter implements CoordinatePublisher {

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

