package pse.nebula.uservehicleservice.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import pse.nebula.uservehicleservice.infrastructure.adapter.outbound.websocket.VehicleTelemetryWebSocketHandler;

/**
 * WebSocket Configuration for real-time vehicle telemetry.
 * Registers the WebSocket handler at /ws/vehicle-telemetry endpoint.
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final VehicleTelemetryWebSocketHandler telemetryHandler;

    public WebSocketConfig(VehicleTelemetryWebSocketHandler telemetryHandler) {
        this.telemetryHandler = telemetryHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(telemetryHandler, "/ws/vehicle-telemetry");
    }
}
