package pse.nebula.telemetry.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * MQTT Configuration Properties
 *
 * This class binds MQTT configuration properties from application.properties
 * and provides validation for required fields.
 */
@Configuration
@ConfigurationProperties(prefix = "mqtt")
@Validated
public class MqttProperties {

    private Broker broker = new Broker();
    private Client client = new Client();
    private Topic topic = new Topic();

    public Broker getBroker() {
        return broker;
    }

    public void setBroker(Broker broker) {
        this.broker = broker;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public static class Broker {
        @NotBlank(message = "MQTT broker host must not be blank")
        private String host = "localhost";

        @NotNull(message = "MQTT broker port must not be null")
        private Integer port = 1883;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }
    }

    public static class Client {
        @NotBlank(message = "MQTT client ID must not be blank")
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public static class Topic {
        @NotBlank(message = "MQTT telemetry topic must not be blank")
        private String telemetry = "telemetry/data";

        public String getTelemetry() {
            return telemetry;
        }

        public void setTelemetry(String telemetry) {
            this.telemetry = telemetry;
        }
    }
}