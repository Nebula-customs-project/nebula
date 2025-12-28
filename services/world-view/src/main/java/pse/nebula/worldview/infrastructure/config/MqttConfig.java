package pse.nebula.worldview.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pse.nebula.worldview.infrastructure.adapter.messaging.MqttCoordinatePublisher;
import pse.nebula.worldview.infrastructure.adapter.web.mapper.DtoMapper;

import java.util.UUID;

/**
 * MQTT Configuration for RabbitMQ messaging.
 * Connects to RabbitMQ's MQTT plugin for publishing journey coordinate updates.
 */
@Slf4j
@Configuration
public class MqttConfig {

    @Value("${mqtt.broker.host:localhost}")
    private String brokerHost;

    @Value("${mqtt.broker.port:1883}")
    private int brokerPort;

    @Value("${mqtt.client.id:world-view-service}")
    private String clientIdPrefix;

    @Value("${mqtt.username:nebula_user}")
    private String username;

    @Value("${mqtt.password:nebula@2025}")
    private String password;

    @Value("${mqtt.topic.prefix:nebula/journey}")
    private String topicPrefix;

    @Bean
    @ConditionalOnProperty(name = "mqtt.enabled", havingValue = "true", matchIfMissing = true)
    public Mqtt5AsyncClient mqttClient() {
        String clientId = clientIdPrefix + "-" + UUID.randomUUID().toString().substring(0, 8);
        
        log.info("Connecting to MQTT broker at {}:{} with client ID: {}", brokerHost, brokerPort, clientId);

        Mqtt5AsyncClient client = Mqtt5Client.builder()
                .identifier(clientId)
                .serverHost(brokerHost)
                .serverPort(brokerPort)
                .automaticReconnectWithDefaultConfig()
                .build()
                .toAsync();

        // Connect with authentication
        client.connectWith()
                .simpleAuth()
                .username(username)
                .password(password.getBytes())
                .applySimpleAuth()
                .send()
                .whenComplete((connAck, throwable) -> {
                    if (throwable != null) {
                        log.error("Failed to connect to MQTT broker: {}", throwable.getMessage());
                    } else {
                        log.info("Successfully connected to MQTT broker. Reason: {}", 
                                connAck.getReasonCode());
                    }
                });

        return client;
    }

    @Bean
    @ConditionalOnProperty(name = "mqtt.enabled", havingValue = "true", matchIfMissing = true)
    public MqttCoordinatePublisher mqttCoordinatePublisher(
            Mqtt5AsyncClient mqttClient, 
            DtoMapper dtoMapper, 
            ObjectMapper objectMapper) {
        log.info("Creating MqttCoordinatePublisher with topic prefix: {}", topicPrefix);
        return new MqttCoordinatePublisher(mqttClient, dtoMapper, objectMapper, topicPrefix);
    }
}
