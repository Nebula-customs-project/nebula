package pse.nebula.uservehicleservice.infrastructure.adapter.outbound.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import pse.nebula.uservehicleservice.application.service.UserVehicleAssignmentService;
import pse.nebula.uservehicleservice.domain.model.UserVehicle;
import pse.nebula.uservehicleservice.infrastructure.config.WebSocketProperties;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VehicleTelemetryWebSocketHandler Unit Tests")
class VehicleTelemetryWebSocketHandlerTest {

    private static final String USER_ID = "user-123";
    private static final String VEHICLE_NAME = "Furari";

    @Mock
    private UserVehicleAssignmentService assignmentService;

    @Mock
    private WebSocketSession session;

    private ObjectMapper objectMapper;
    private WebSocketProperties webSocketProperties;
    private VehicleTelemetryWebSocketHandler handler;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        webSocketProperties = new WebSocketProperties();
        webSocketProperties.setEndpoint("/ws/vehicle-telemetry");
        webSocketProperties.setIntervalMinutes(5);
        webSocketProperties.setMaxConnectionsPerUser(1);

        handler = new VehicleTelemetryWebSocketHandler(assignmentService, objectMapper, webSocketProperties);
    }

    @Test
    @DisplayName("should establish connection and start telemetry for valid user")
    void shouldEstablishConnectionForValidUser() throws Exception {
        // Given
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-User-Id", USER_ID);
        when(session.getHandshakeHeaders()).thenReturn(headers);
        when(session.isOpen()).thenReturn(true);

        LocalDate maintenanceDate = LocalDate.now().plusMonths(6);
        UserVehicle userVehicle = new UserVehicle(USER_ID, 1, VEHICLE_NAME, maintenanceDate);
        when(assignmentService.getOrAssignVehicle(USER_ID)).thenReturn(userVehicle);

        // When
        handler.afterConnectionEstablished(session);

        // Then
        assertThat(handler.isConnected(USER_ID)).isTrue();
        assertThat(handler.getActiveConnectionCount()).isEqualTo(1);
        verify(assignmentService).getOrAssignVehicle(USER_ID);
    }

    @Test
    @DisplayName("should reject connection when user ID is missing")
    void shouldRejectConnectionWhenUserIdMissing() throws Exception {
        // Given
        HttpHeaders headers = new HttpHeaders();
        when(session.getHandshakeHeaders()).thenReturn(headers);

        // When
        handler.afterConnectionEstablished(session);

        // Then
        verify(session).close(any(CloseStatus.class));
        assertThat(handler.getActiveConnectionCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("should close existing connection when same user connects again")
    void shouldCloseExistingConnectionOnNewConnection() throws Exception {
        // Given
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-User-Id", USER_ID);

        WebSocketSession session1 = mock(WebSocketSession.class);
        WebSocketSession session2 = mock(WebSocketSession.class);

        when(session1.getHandshakeHeaders()).thenReturn(headers);
        when(session2.getHandshakeHeaders()).thenReturn(headers);
        when(session1.isOpen()).thenReturn(true);
        when(session2.isOpen()).thenReturn(true);

        LocalDate maintenanceDate = LocalDate.now().plusMonths(6);
        UserVehicle userVehicle = new UserVehicle(USER_ID, 1, VEHICLE_NAME, maintenanceDate);
        when(assignmentService.getOrAssignVehicle(USER_ID)).thenReturn(userVehicle);

        // When
        handler.afterConnectionEstablished(session1);
        handler.afterConnectionEstablished(session2);

        // Then - first session should be closed
        verify(session1).close(any(CloseStatus.class));
        assertThat(handler.getActiveConnectionCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("should remove connection on close")
    void shouldRemoveConnectionOnClose() throws Exception {
        // Given
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-User-Id", USER_ID);
        when(session.getHandshakeHeaders()).thenReturn(headers);
        when(session.isOpen()).thenReturn(true);

        LocalDate maintenanceDate = LocalDate.now().plusMonths(6);
        UserVehicle userVehicle = new UserVehicle(USER_ID, 1, VEHICLE_NAME, maintenanceDate);
        when(assignmentService.getOrAssignVehicle(USER_ID)).thenReturn(userVehicle);

        handler.afterConnectionEstablished(session);
        assertThat(handler.isConnected(USER_ID)).isTrue();

        // When
        handler.afterConnectionClosed(session, CloseStatus.NORMAL);

        // Then
        assertThat(handler.isConnected(USER_ID)).isFalse();
        assertThat(handler.getActiveConnectionCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("should handle multiple users")
    void shouldHandleMultipleUsers() throws Exception {
        // Given
        String user1 = "user-1";
        String user2 = "user-2";

        HttpHeaders headers1 = new HttpHeaders();
        headers1.add("X-User-Id", user1);
        HttpHeaders headers2 = new HttpHeaders();
        headers2.add("X-User-Id", user2);

        WebSocketSession session1 = mock(WebSocketSession.class);
        WebSocketSession session2 = mock(WebSocketSession.class);

        when(session1.getHandshakeHeaders()).thenReturn(headers1);
        when(session2.getHandshakeHeaders()).thenReturn(headers2);
        when(session1.isOpen()).thenReturn(true);
        when(session2.isOpen()).thenReturn(true);

        LocalDate maintenanceDate = LocalDate.now().plusMonths(6);
        when(assignmentService.getOrAssignVehicle(user1))
                .thenReturn(new UserVehicle(user1, 1, "Furari", maintenanceDate));
        when(assignmentService.getOrAssignVehicle(user2))
                .thenReturn(new UserVehicle(user2, 2, "GTR", maintenanceDate));

        // When
        handler.afterConnectionEstablished(session1);
        handler.afterConnectionEstablished(session2);

        // Then
        assertThat(handler.getActiveConnectionCount()).isEqualTo(2);
        assertThat(handler.isConnected(user1)).isTrue();
        assertThat(handler.isConnected(user2)).isTrue();
    }

    @Test
    @DisplayName("should cleanup on shutdown")
    void shouldCleanupOnShutdown() throws Exception {
        // Given
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-User-Id", USER_ID);
        when(session.getHandshakeHeaders()).thenReturn(headers);
        when(session.isOpen()).thenReturn(true);

        LocalDate maintenanceDate = LocalDate.now().plusMonths(6);
        UserVehicle userVehicle = new UserVehicle(USER_ID, 1, VEHICLE_NAME, maintenanceDate);
        when(assignmentService.getOrAssignVehicle(USER_ID)).thenReturn(userVehicle);

        handler.afterConnectionEstablished(session);

        // When
        handler.shutdown();

        // Then
        assertThat(handler.getActiveConnectionCount()).isEqualTo(0);
    }
}
