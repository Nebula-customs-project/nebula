package pse.nebula.user.service;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MqttService {

    @Value("${mqtt.broker.host}")
    private String brokerHost;

    @Value("${mqtt.broker.port}")
    private int brokerPort;

    @Value("${mqtt.topics.vehicle-data}")
    private String vehicleTopic;

    private Mqtt3AsyncClient client;

    @PostConstruct
    public void connect() {
        client = MqttClient.builder()
                .useMqttVersion3()
                .serverHost(brokerHost)
                .serverPort(brokerPort)
                .buildAsync();

        client.connect().whenComplete((connAck, throwable) -> {
            if (throwable != null) {
                System.err.println("Failed to connect to MQTT broker: " + throwable.getMessage());
            } else {
                System.out.println("Connected to MQTT broker");
                subscribeToVehicleData();
            }
        });
    }

    private void subscribeToVehicleData() {
        client.subscribeWith()
                .topicFilter(vehicleTopic)
                .callback(publish -> {
                    String payload = new String(publish.getPayloadAsBytes());
                    System.out.println("Received vehicle data: " + payload);
                    // Parse and update vehicle in DB
                    // For now, just log
                })
                .send();
    }
}