package pse.nebula.worldview.infrastructure.adapter.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import pse.nebula.worldview.domain.model.Coordinate;
import pse.nebula.worldview.domain.model.JourneyState;
import pse.nebula.worldview.domain.port.outbound.CoordinatePublisher;

import java.util.ArrayList;
import java.util.List;

/**
 * Composite implementation of CoordinatePublisher that delegates to multiple publishers.
 * This allows coordinate updates to be sent via both SSE (for browser EventSource API)
 * and MQTT (for RabbitMQ WebSocket MQTT clients).
 * 
 * Publishers are invoked in parallel - if one fails, others continue.
 */
@Slf4j
@Primary
@Component
public class CompositeCoordinatePublisher implements CoordinatePublisher {

    private final List<CoordinatePublisher> publishers;

    public CompositeCoordinatePublisher(
            SseCoordinatePublisher ssePublisher,
            @Autowired(required = false) MqttCoordinatePublisher mqttPublisher) {
        
        this.publishers = new ArrayList<>();
        publishers.add(ssePublisher);
        
        // MQTT publisher is optional - only added if MQTT is enabled
        if (mqttPublisher != null) {
            publishers.add(mqttPublisher);
        }
        
        log.info("Initialized CompositeCoordinatePublisher with {} active publishers: {}", 
                publishers.size(),
                publishers.stream().map(p -> p.getClass().getSimpleName()).toList());
    }

    @Override
    public void publishCoordinateUpdate(String journeyId, Coordinate coordinate, JourneyState journeyState) {
        for (CoordinatePublisher publisher : publishers) {
            try {
                publisher.publishCoordinateUpdate(journeyId, coordinate, journeyState);
            } catch (Exception e) {
                log.error("Failed to publish coordinate update via {}: {}", 
                        publisher.getClass().getSimpleName(), e.getMessage());
            }
        }
    }

    @Override
    public void publishJourneyStarted(JourneyState journeyState) {
        for (CoordinatePublisher publisher : publishers) {
            try {
                publisher.publishJourneyStarted(journeyState);
            } catch (Exception e) {
                log.error("Failed to publish journey started via {}: {}", 
                        publisher.getClass().getSimpleName(), e.getMessage());
            }
        }
    }

    @Override
    public void publishJourneyCompleted(JourneyState journeyState) {
        for (CoordinatePublisher publisher : publishers) {
            try {
                publisher.publishJourneyCompleted(journeyState);
            } catch (Exception e) {
                log.error("Failed to publish journey completed via {}: {}", 
                        publisher.getClass().getSimpleName(), e.getMessage());
            }
        }
    }
}
