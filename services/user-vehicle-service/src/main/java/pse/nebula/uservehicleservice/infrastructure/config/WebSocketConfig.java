package pse.nebula.uservehicleservice.infrastructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import pse.nebula.uservehicleservice.infrastructure.adapter.outbound.websocket.VehicleTelemetryWebSocketHandler;

/**
 * WebSocket Configuration for real-time vehicle telemetry.
 * Registers the WebSocket handler at the configured endpoint path.
 * 
 * Endpoint path is externalized via WebSocketProperties for flexibility.
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private static final Logger log = LoggerFactory.getLogger(WebSocketConfig.class);

    private final VehicleTelemetryWebSocketHandler telemetryHandler;
    private final WebSocketProperties webSocketProperties;

    public WebSocketConfig(VehicleTelemetryWebSocketHandler telemetryHandler,
            WebSocketProperties webSocketProperties) {
        this.telemetryHandler = telemetryHandler;
        this.webSocketProperties = webSocketProperties;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        String endpoint = webSocketProperties.getEndpoint();
        registry.addHandler(telemetryHandler, endpoint);
        log.info("Registered WebSocket handler at endpoint: {}", endpoint);
    }
}
