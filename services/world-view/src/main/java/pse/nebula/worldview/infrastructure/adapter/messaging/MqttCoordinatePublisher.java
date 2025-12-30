package pse.nebula.worldview.infrastructure.adapter.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import lombok.extern.slf4j.Slf4j;
import pse.nebula.worldview.domain.model.Coordinate;
import pse.nebula.worldview.domain.model.JourneyState;
import pse.nebula.worldview.domain.port.outbound.CoordinatePublisher;
import pse.nebula.worldview.infrastructure.adapter.web.dto.CoordinateUpdateDto;
import pse.nebula.worldview.infrastructure.adapter.web.mapper.DtoMapper;

import java.nio.charset.StandardCharsets;

/**
 * MQTT-based implementation of CoordinatePublisher.
 * Publishes coordinate updates to MQTT topics via RabbitMQ's MQTT plugin.
 * This allows frontend clients to subscribe via WebSocket MQTT.
 * 
 * Topic structure:
 * - nebula/journey/{journeyId}/position - Real-time coordinate updates
 * - nebula/journey/{journeyId}/events - Journey lifecycle events (started, completed, paused)
 */
@Slf4j
public class MqttCoordinatePublisher implements CoordinatePublisher {

    private final Mqtt5AsyncClient mqttClient;
    private final DtoMapper dtoMapper;
    private final ObjectMapper objectMapper;
    private final String topicPrefix;

    public MqttCoordinatePublisher(Mqtt5AsyncClient mqttClient, DtoMapper dtoMapper, 
            ObjectMapper objectMapper, String topicPrefix) {
        this.mqttClient = mqttClient;
        this.dtoMapper = dtoMapper;
        this.objectMapper = objectMapper;
        this.topicPrefix = topicPrefix;
    }

    @Override
    public void publishCoordinateUpdate(String journeyId, Coordinate coordinate, JourneyState journeyState) {
        CoordinateUpdateDto update = dtoMapper.toCoordinateUpdate(journeyState);
        String topic = topicPrefix + "/" + journeyId + "/position";
        
        publishMessage(topic, update, "coordinate update");
        
        log.debug("MQTT: Published coordinate update for journey: {} to topic: {} - Waypoint {}/{}",
                journeyId, topic,
                journeyState.getCurrentWaypointIndex() + 1,
                journeyState.getRoute().waypoints().size());
    }

    @Override
    public void publishJourneyStarted(JourneyState journeyState) {
        CoordinateUpdateDto update = dtoMapper.toCoordinateUpdate(journeyState);
        String topic = topicPrefix + "/" + journeyState.getJourneyId() + "/events";
        
        JourneyEventMessage event = new JourneyEventMessage("STARTED", update);
        publishMessage(topic, event, "journey started event");
        
        log.info("Published MQTT journey started event for: {} to topic: {}", 
                journeyState.getJourneyId(), topic);
    }

    @Override
    public void publishJourneyCompleted(JourneyState journeyState) {
        CoordinateUpdateDto update = dtoMapper.toCoordinateUpdate(journeyState);
        String topic = topicPrefix + "/" + journeyState.getJourneyId() + "/events";
        
        JourneyEventMessage event = new JourneyEventMessage("COMPLETED", update);
        publishMessage(topic, event, "journey completed event");
        
        log.info("Published MQTT journey completed event for: {} to topic: {}", 
                journeyState.getJourneyId(), topic);
    }

    private void publishMessage(String topic, Object payload, String messageType) {
        // Run MQTT publishing in a separate thread to avoid blocking the main request thread
        // This ensures SSE works even if MQTT is slow or unavailable
        new Thread(() -> {
            try {
                String jsonPayload = objectMapper.writeValueAsString(payload);
                
                mqttClient.publishWith()
                        .topic(topic)
                        .payload(jsonPayload.getBytes(StandardCharsets.UTF_8))
                        .retain(false)
                        .send()
                        .whenComplete((publish, throwable) -> {
                            if (throwable != null) {
                                log.warn("Failed to publish {} to topic {}: {}", 
                                        messageType, topic, throwable.getMessage());
                            } else {
                                log.trace("Successfully published {} to topic: {}", messageType, topic);
                            }
                        });
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize {} for MQTT: {}", messageType, e.getMessage());
            } catch (Exception e) {
                log.warn("MQTT publishing failed for {}: {}", messageType, e.getMessage());
            }
        }, "mqtt-publisher").start();
    }

    /**
     * Wrapper for journey lifecycle events.
     */
    public record JourneyEventMessage(String eventType, CoordinateUpdateDto data) {}
}
