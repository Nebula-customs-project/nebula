package pse.nebula.uservehicleservice.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pse.nebula.uservehicleservice.domain.model.UserVehicle;
import pse.nebula.uservehicleservice.domain.repository.UserVehicleRepository;
import pse.nebula.uservehicleservice.infrastructure.adapter.outbound.rest.VehicleServiceClient;
import pse.nebula.uservehicleservice.infrastructure.adapter.outbound.rest.dto.VehicleDto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for User Vehicle Service.
 * Note: WebSocket telemetry is tested separately in
 * VehicleTelemetryWebSocketHandlerTest.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("User Vehicle Service Integration Tests")
class UserVehicleIntegrationTest {

    private static final String USER_VEHICLE_INFO_URL = "/api/v1/user-vehicle/info";
    private static final String USER_ID_HEADER = "X-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserVehicleRepository userVehicleRepository;

    @MockitoBean
    private VehicleServiceClient vehicleServiceClient;

    @BeforeEach
    void setUp() {
        userVehicleRepository.deleteAll();
    }

    @Test
    @DisplayName("should assign new vehicle for new user and persist to database")
    void shouldAssignNewVehicleForNewUser() throws Exception {
        // Given
        String userId = "new-user-001";
        List<VehicleDto> availableVehicles = List.of(
                new VehicleDto(1, "Furari", "SPORTS", 670, new BigDecimal("245000.00"), "furari-hero",
                        "/models/furarri.glb"));
        when(vehicleServiceClient.getAllVehicles()).thenReturn(availableVehicles);

        // When
        mockMvc.perform(get(USER_VEHICLE_INFO_URL)
                .header(USER_ID_HEADER, userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maintenanceDueDate").exists())
                .andExpect(jsonPath("$.tyrePressures").exists());

        // Then - verify persisted to database
        assertThat(userVehicleRepository.findByUserId(userId)).isPresent();
        UserVehicle savedVehicle = userVehicleRepository.findByUserId(userId).get();
        assertThat(savedVehicle.getVehicleName()).isEqualTo("Furari");
        assertThat(savedVehicle.getVehicleId()).isEqualTo(1);
        assertThat(savedVehicle.getMaintenanceDueDate()).isEqualTo(LocalDate.now().plusMonths(6));
    }

    @Test
    @DisplayName("should return existing vehicle for returning user without calling vehicle service")
    void shouldReturnExistingVehicleForReturningUser() throws Exception {
        // Given - pre-create user vehicle assignment
        String userId = "existing-user-001";
        LocalDate maintenanceDate = LocalDate.of(2026, 7, 18);
        UserVehicle existingVehicle = new UserVehicle(userId, 2, "GTR", maintenanceDate);
        userVehicleRepository.save(existingVehicle);

        // When
        mockMvc.perform(get(USER_VEHICLE_INFO_URL)
                .header(USER_ID_HEADER, userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maintenanceDueDate").value("2026-07-18"))
                .andExpect(jsonPath("$.tyrePressures.frontLeft", greaterThanOrEqualTo(28.0)))
                .andExpect(jsonPath("$.tyrePressures.frontLeft", lessThanOrEqualTo(35.0)));

        // Then - verify vehicle service was NOT called
        verify(vehicleServiceClient, never()).getAllVehicles();
    }

    @Test
    @DisplayName("should generate different tyre pressures on each request")
    void shouldGenerateDifferentTyrePressuresOnEachRequest() throws Exception {
        // Given
        String userId = "tyre-test-user";
        LocalDate maintenanceDate = LocalDate.now().plusMonths(6);
        UserVehicle existingVehicle = new UserVehicle(userId, 1, "Furari", maintenanceDate);
        userVehicleRepository.save(existingVehicle);
        ObjectMapper objectMapper = new ObjectMapper();

        // When - make two requests
        String response1 = mockMvc.perform(get(USER_VEHICLE_INFO_URL)
                .header(USER_ID_HEADER, userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String response2 = mockMvc.perform(get(USER_VEHICLE_INFO_URL)
                .header(USER_ID_HEADER, userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // Then - deserialize and compare tyre pressures
        JsonNode json1 = objectMapper.readTree(response1).get("tyrePressures");
        JsonNode json2 = objectMapper.readTree(response2).get("tyrePressures");

        // At least one tyre pressure should differ between requests (high probability
        // with random generation)
        boolean anyDifferent = !json1.get("frontLeft").equals(json2.get("frontLeft")) ||
                !json1.get("frontRight").equals(json2.get("frontRight")) ||
                !json1.get("rearLeft").equals(json2.get("rearLeft")) ||
                !json1.get("rearRight").equals(json2.get("rearRight"));

        assertThat(anyDifferent)
                .as("Tyre pressures should differ between requests (random generation)")
                .isTrue();
    }

    @Test
    @DisplayName("should return same vehicle on repeated requests (idempotent)")
    void shouldReturnSameVehicleOnRepeatedRequests() throws Exception {
        // Given
        String userId = "repeated-request-user";
        List<VehicleDto> availableVehicles = List.of(
                new VehicleDto(1, "Furari", "SPORTS", 670, new BigDecimal("245000.00"), "furari-hero",
                        "/models/furarri.glb"));
        when(vehicleServiceClient.getAllVehicles()).thenReturn(availableVehicles);

        // When - make multiple sequential requests for the same user
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get(USER_VEHICLE_INFO_URL)
                    .header(USER_ID_HEADER, userId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }

        // Then - should only have one entry in database (idempotent assignment)
        long count = userVehicleRepository.count();
        assertThat(count).isEqualTo(1);

        // Verify vehicle service was called only once (for first request)
        verify(vehicleServiceClient, times(1)).getAllVehicles();
    }

    @Test
    @DisplayName("should return correct response structure")
    void shouldReturnCorrectResponseStructure() throws Exception {
        // Given
        String userId = "structure-test-user";
        LocalDate maintenanceDate = LocalDate.of(2026, 8, 15);
        UserVehicle existingVehicle = new UserVehicle(userId, 1, "Furari", maintenanceDate);
        userVehicleRepository.save(existingVehicle);

        // When/Then
        mockMvc.perform(get(USER_VEHICLE_INFO_URL)
                .header(USER_ID_HEADER, userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.maintenanceDueDate").value("2026-08-15"))
                .andExpect(jsonPath("$.tyrePressures").isMap())
                .andExpect(jsonPath("$.tyrePressures.frontLeft").isNumber())
                .andExpect(jsonPath("$.tyrePressures.frontRight").isNumber())
                .andExpect(jsonPath("$.tyrePressures.rearLeft").isNumber())
                .andExpect(jsonPath("$.tyrePressures.rearRight").isNumber());
    }
}
