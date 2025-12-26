package pse.nebula.telemetry.infrastructure.messaging.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pse.nebula.telemetry.domain.Simulation;
import pse.nebula.telemetry.domain.TelemetryPublisherPort;
import pse.nebula.telemetry.infrastructure.messaging.dto.TelemetryMessageDto;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

/**
 * MQTT adapter that implements the TelemetryPublisherPort.
 * Publishes telemetry data to the Mosquitto broker asynchronously.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MqttTelemetryPublisher implements TelemetryPublisherPort {

    private static final String TOPIC_TEMPLATE = "vehicle/%s/telemetry";

    private final Mqtt5AsyncClient mqttClient;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(Simulation simulation) {
        try {
            // Convert domain object to DTO
            TelemetryMessageDto message = buildTelemetryMessage(simulation);

            // Serialize to JSON
            String jsonPayload = objectMapper.writeValueAsString(message);

            // Build topic
            String topic = String.format(TOPIC_TEMPLATE, simulation.getVehicleId());

            // Publish asynchronously
            Mqtt5Publish publish = Mqtt5Publish.builder()
                .topic(topic)
                .payload(jsonPayload.getBytes(StandardCharsets.UTF_8))
                .retain(false)
                .qos(com.hivemq.client.mqtt.datatypes.MqttQos.AT_LEAST_ONCE)
                .build();

            mqttClient.publish(publish)
                .whenComplete((mqtt5PublishResult, throwable) -> {
                    if (throwable != null) {
                        log.error("Failed to publish telemetry for vehicle: {} to topic: {}",
                            simulation.getVehicleId(), topic, throwable);
                    } else {
                        log.debug("Successfully published telemetry for vehicle: {} to topic: {}",
                            simulation.getVehicleId(), topic);
                    }
                });

        } catch (Exception e) {
            log.error("Error preparing telemetry message for vehicle: {}",
                simulation.getVehicleId(), e);
            throw new RuntimeException("Failed to publish telemetry", e);
        }
    }

    private TelemetryMessageDto buildTelemetryMessage(Simulation simulation) {
        return TelemetryMessageDto.builder()
            .vehicleId(simulation.getVehicleId())
            .timestamp(Instant.now())
            .location(TelemetryMessageDto.LocationDto.builder()
                .latitude(simulation.getCurrentLocation().lat())
                .longitude(simulation.getCurrentLocation().lng())
                .build())
            .speedMps(simulation.getSpeed())
            .status(simulation.getStatus().name())
            .build();
    }
}

