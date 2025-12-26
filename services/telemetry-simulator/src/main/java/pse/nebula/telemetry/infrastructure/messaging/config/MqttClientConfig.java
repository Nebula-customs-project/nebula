package pse.nebula.telemetry.infrastructure.messaging.config;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pse.nebula.telemetry.infrastructure.config.MqttProperties;

import jakarta.annotation.PreDestroy;

/**
 * Configuration for MQTT Client.
 * Creates an asynchronous MQTT client that connects to the Mosquitto broker.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class MqttClientConfig {

    private final MqttProperties mqttProperties;
    private Mqtt5AsyncClient mqttClient;

    @Bean
    public Mqtt5AsyncClient mqtt5AsyncClient() {
        String brokerHost = mqttProperties.getBroker().getHost();
        int brokerPort = mqttProperties.getBroker().getPort();
        String clientId = mqttProperties.getClient().getId();

        log.info("Initializing MQTT client - Broker: {}:{}, ClientId: {}",
            brokerHost, brokerPort, clientId);

        mqttClient = MqttClient.builder()
            .useMqttVersion5()
            .identifier(clientId)
            .serverHost(brokerHost)
            .serverPort(brokerPort)
            .automaticReconnect()
                .initialDelay(1, java.util.concurrent.TimeUnit.SECONDS)
                .maxDelay(30, java.util.concurrent.TimeUnit.SECONDS)
                .applyAutomaticReconnect()
            .buildAsync();

        // Connect asynchronously
        mqttClient.connect()
            .whenComplete((connAck, throwable) -> {
                if (throwable != null) {
                    log.error("Failed to connect to MQTT broker at {}:{}", brokerHost, brokerPort, throwable);
                } else {
                    log.info("Successfully connected to MQTT broker at {}:{}", brokerHost, brokerPort);
                }
            });

        return mqttClient;
    }

    @PreDestroy
    public void cleanup() {
        if (mqttClient != null && mqttClient.getState().isConnected()) {
            log.info("Disconnecting MQTT client");
            mqttClient.disconnect().whenComplete((unused, throwable) -> {
                if (throwable != null) {
                    log.error("Error disconnecting MQTT client", throwable);
                } else {
                    log.info("MQTT client disconnected successfully");
                }
            });
        }
    }
}

