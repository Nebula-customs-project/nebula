package pse.nebula.worldview.infrastructure.adapter.outbound.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import pse.nebula.worldview.domain.model.Coordinate;
import pse.nebula.worldview.domain.model.JourneyState;
import pse.nebula.worldview.domain.port.outbound.CoordinatePublisher;

/**
 * No-operation implementation of CoordinatePublisher.
 * Used when MQTT is disabled (e.g., in tests or when RabbitMQ is not available).
 * Logs the updates but does not actually publish them anywhere.
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "mqtt.enabled", havingValue = "false")
public class NoOpCoordinatePublisherAdapter implements CoordinatePublisher {

    public NoOpCoordinatePublisherAdapter() {
        log.info("NoOpCoordinatePublisherAdapter initialized - MQTT publishing is disabled");
    }

    @Override
    public void publishCoordinateUpdate(String journeyId, Coordinate coordinate, JourneyState journeyState) {
        log.debug("NoOp: Would publish coordinate update for journey: {} - Position: [{}, {}]",
                journeyId, coordinate.latitude(), coordinate.longitude());
    }

    @Override
    public void publishJourneyStarted(JourneyState journeyState) {
        log.debug("NoOp: Would publish journey started for: {}", journeyState.getJourneyId());
    }

    @Override
    public void publishJourneyCompleted(JourneyState journeyState) {
        log.debug("NoOp: Would publish journey completed for: {}", journeyState.getJourneyId());
    }
}

