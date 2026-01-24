package pse.nebula.uservehicleservice.infrastructure.adapter.outbound.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import pse.nebula.uservehicleservice.application.service.UserVehicleAssignmentService;
import pse.nebula.uservehicleservice.domain.model.UserVehicle;
import pse.nebula.uservehicleservice.infrastructure.adapter.outbound.websocket.dto.VehicleTelemetryDto;
import pse.nebula.uservehicleservice.infrastructure.config.WebSocketProperties;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.*;

/**
 * WebSocket Handler for vehicle telemetry data.
 * Sends vehicle location and fuel data to connected clients.
 * Manages scheduled publishing every 5 minutes per active user.
 * 
 * Only one WebSocket connection is allowed per user.
 * New connections for the same user will close the previous connection.
 */
@Slf4j
@Component
public class VehicleTelemetryWebSocketHandler extends TextWebSocketHandler {

    private static final String USER_ID_HEADER = "X-User-Id";

    // Fixed location (Stuttgart area)
    private static final double FIXED_LATITUDE = 48.7758;
    private static final double FIXED_LONGITUDE = 9.1829;

    // Fixed fuel level
    private static final BigDecimal FIXED_FUEL = BigDecimal.valueOf(75.0);

    private final UserVehicleAssignmentService assignmentService;
    private final ObjectMapper objectMapper;
    private final WebSocketProperties webSocketProperties;
    private final ScheduledExecutorService scheduler;
    private final Map<String, WebSocketSession> userSessions;
    private final Map<String, ScheduledFuture<?>> activePublishers;

    public VehicleTelemetryWebSocketHandler(
            UserVehicleAssignmentService assignmentService,
            ObjectMapper objectMapper,
            WebSocketProperties webSocketProperties) {
        this.assignmentService = assignmentService;
        this.objectMapper = objectMapper;
        this.webSocketProperties = webSocketProperties;
        this.scheduler = Executors.newScheduledThreadPool(4, r -> {
            Thread thread = new Thread(r, "ws-telemetry-publisher");
            thread.setDaemon(true);
            return thread;
        });
        this.userSessions = new ConcurrentHashMap<>();
        this.activePublishers = new ConcurrentHashMap<>();
        log.info("VehicleTelemetryWebSocketHandler initialized with config: {}", webSocketProperties);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = extractUserId(session);

        if (userId == null || userId.isBlank()) {
            log.warn("WebSocket connection rejected: missing X-User-Id header");
            session.close(CloseStatus.POLICY_VIOLATION.withReason("Missing user ID"));
            return;
        }

        log.info("WebSocket connection established for user: {}", userId);

        // Close existing connection if any (one connection per user)
        WebSocketSession existingSession = userSessions.get(userId);
        if (existingSession != null && existingSession.isOpen()) {
            log.info("Closing existing WebSocket connection for user: {}", userId);
            stopTelemetryUpdates(userId);
            try {
                existingSession.close(CloseStatus.POLICY_VIOLATION.withReason("New connection opened"));
            } catch (IOException e) {
                log.warn("Error closing existing session for user {}: {}", userId, e.getMessage());
            }
        }

        // Store new session
        userSessions.put(userId, session);

        // Get or assign vehicle for the user
        UserVehicle userVehicle = assignmentService.getOrAssignVehicle(userId);
        String vehicleName = userVehicle.getVehicleName();

        log.info("Starting telemetry updates for user: {} with vehicle: {}", userId, vehicleName);

        // Send immediate telemetry update
        sendTelemetry(session, userId, vehicleName);

        // Schedule periodic updates using configured interval
        long intervalMinutes = webSocketProperties.getIntervalMinutes();
        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(
                () -> sendTelemetry(session, userId, vehicleName),
                intervalMinutes,
                intervalMinutes,
                TimeUnit.MINUTES);
        activePublishers.put(userId, future);

        log.info("Scheduled telemetry updates for user: {} every {} minutes", userId, intervalMinutes);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String userId = extractUserId(session);
        if (userId != null) {
            log.info("WebSocket connection closed for user: {} with status: {}", userId, status);
            userSessions.remove(userId);
            stopTelemetryUpdates(userId);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        String userId = extractUserId(session);
        log.error("WebSocket transport error for user {}: {}", userId, exception.getMessage());
    }

    /**
     * Extracts user ID from the X-User-Id header injected by the gateway.
     */
    private String extractUserId(WebSocketSession session) {
        HttpHeaders headers = session.getHandshakeHeaders();
        return headers.getFirst(USER_ID_HEADER);
    }

    /**
     * Sends telemetry data to the WebSocket session.
     */
    private void sendTelemetry(WebSocketSession session, String userId, String vehicleName) {
        if (!session.isOpen()) {
            log.debug("Session closed, stopping telemetry for user: {}", userId);
            stopTelemetryUpdates(userId);
            return;
        }

        VehicleTelemetryDto telemetry = new VehicleTelemetryDto(
                vehicleName,
                new VehicleTelemetryDto.LocationDto(FIXED_LATITUDE, FIXED_LONGITUDE),
                FIXED_FUEL,
                Instant.now());

        try {
            String payload = objectMapper.writeValueAsString(telemetry);
            session.sendMessage(new TextMessage(payload));
            log.debug("Sent telemetry to user {}: {}", userId, payload);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize telemetry for user {}: {}", userId, e.getMessage());
        } catch (IOException e) {
            log.error("Failed to send telemetry to user {}: {}", userId, e.getMessage());
            stopTelemetryUpdates(userId);
        }
    }

    /**
     * Stops scheduled telemetry updates for a user.
     */
    private void stopTelemetryUpdates(String userId) {
        ScheduledFuture<?> future = activePublishers.remove(userId);
        if (future != null) {
            future.cancel(false);
            log.info("Stopped telemetry updates for user: {}", userId);
        }
    }

    /**
     * Checks if a user has an active WebSocket connection.
     */
    public boolean isConnected(String userId) {
        WebSocketSession session = userSessions.get(userId);
        return session != null && session.isOpen();
    }

    /**
     * Gets the count of active connections.
     */
    public int getActiveConnectionCount() {
        return userSessions.size();
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down VehicleTelemetryWebSocketHandler with {} active connections", userSessions.size());

        // Cancel all scheduled tasks
        activePublishers.values().forEach(future -> future.cancel(false));
        activePublishers.clear();

        // Close all sessions
        userSessions.values().forEach(session -> {
            try {
                if (session.isOpen()) {
                    session.close(CloseStatus.SERVICE_RESTARTED);
                }
            } catch (IOException e) {
                log.warn("Error closing WebSocket session: {}", e.getMessage());
            }
        });
        userSessions.clear();

        // Shutdown scheduler
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }

        log.info("VehicleTelemetryWebSocketHandler shutdown complete");
    }
}
