package pse.nebula.vehicleservice.infrastructure.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pse.nebula.vehicleservice.application.service.ConfigurationService;
import pse.nebula.vehicleservice.application.service.ConfigurationService.VehicleConfiguration;
import pse.nebula.vehicleservice.application.service.VehicleService;
import pse.nebula.vehicleservice.domain.exception.VehicleNotFoundException;
import pse.nebula.vehicleservice.domain.model.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VehicleController.class)
@DisplayName("VehicleController Integration Tests")
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VehicleService vehicleService;

    @MockitoBean
    private ConfigurationService configurationService;

    @Nested
    @DisplayName("GET /api/v1/vehicles")
    class GetAllVehiclesTests {

        @Test
        @DisplayName("should return 200 with list of vehicles")
        void shouldReturnVehiclesList() throws Exception {
            // Given
            Vehicle vehicle = new Vehicle("911 Carrera", CarType.SPORTS, 379, new BigDecimal("106100.00"), "911-carrera-hero");
            Page<Vehicle> vehiclePage = new PageImpl<>(List.of(vehicle));
            when(vehicleService.getAllVehicles(any(Pageable.class))).thenReturn(vehiclePage);

            // When/Then
            mockMvc.perform(get("/api/v1/vehicles")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.vehicles", hasSize(1)))
                    .andExpect(jsonPath("$.vehicles[0].carName", is("911 Carrera")))
                    .andExpect(jsonPath("$.vehicles[0].carType", is("SPORTS")))
                    .andExpect(jsonPath("$.vehicles[0].horsePower", is(379)))
                    .andExpect(jsonPath("$.vehicles[0].basePrice", is(106100.00)))
                    .andExpect(jsonPath("$.vehicles[0].image", is("911-carrera-hero")));
        }

        @Test
        @DisplayName("should return 200 with empty list when no vehicles")
        void shouldReturnEmptyList() throws Exception {
            // Given
            Page<Vehicle> emptyPage = new PageImpl<>(Collections.emptyList());
            when(vehicleService.getAllVehicles(any(Pageable.class))).thenReturn(emptyPage);

            // When/Then
            mockMvc.perform(get("/api/v1/vehicles")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.vehicles", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/vehicles/{id}")
    class GetVehicleByIdTests {

        @Test
        @DisplayName("should return 200 with vehicle details")
        void shouldReturnVehicle() throws Exception {
            // Given
            Vehicle vehicle = new Vehicle("Panamera", CarType.SEDAN, 325, new BigDecimal("92400.00"), "panamera-hero");
            when(vehicleService.getVehicleById(1)).thenReturn(vehicle);

            // When/Then
            mockMvc.perform(get("/api/v1/vehicles/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.carName", is("Panamera")))
                    .andExpect(jsonPath("$.carType", is("SEDAN")))
                    .andExpect(jsonPath("$.horsePower", is(325)));
        }

        @Test
        @DisplayName("should return 404 when vehicle not found")
        void shouldReturn404WhenNotFound() throws Exception {
            // Given
            when(vehicleService.getVehicleById(999)).thenThrow(new VehicleNotFoundException(999));

            // When/Then
            mockMvc.perform(get("/api/v1/vehicles/999")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status", is(404)))
                    .andExpect(jsonPath("$.error", is("Not Found")))
                    .andExpect(jsonPath("$.message", containsString("999")));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/vehicles/{id}/configuration")
    class GetVehicleConfigurationTests {

        @Test
        @DisplayName("should return 200 with configuration options")
        void shouldReturnConfiguration() throws Exception {
            // Given
            Vehicle vehicle = new Vehicle("911 Carrera", CarType.SPORTS, 379, new BigDecimal("106100.00"), "911-carrera-hero");

            Paint paint = new Paint("Black", "Timeless deep black metallic finish");
            paint.addPrice(new PaintPrice(CarType.SPORTS, new BigDecimal("500.00")));

            Rim rim = new Rim("19\" Base Alloy", "Standard wheels", "rim-19-base");
            rim.addPrice(new RimPrice(CarType.SPORTS, BigDecimal.ZERO));

            Interior interior = new Interior("Black Leather", "Classic black leather", "interior-black");
            interior.addPrice(new InteriorPrice(CarType.SPORTS, BigDecimal.ZERO));

            VehicleConfiguration config = new VehicleConfiguration(vehicle, List.of(paint), List.of(rim), List.of(interior), CarType.SPORTS);
            when(configurationService.getConfigurationForVehicle(1)).thenReturn(config);

            // When/Then
            mockMvc.perform(get("/api/v1/vehicles/1/configuration")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.carType", is("SPORTS")))
                    .andExpect(jsonPath("$.basePrice", is(106100.00)))
                    .andExpect(jsonPath("$.paints", hasSize(1)))
                    .andExpect(jsonPath("$.paints[0].name", is("Black")))
                    .andExpect(jsonPath("$.paints[0].price", is(500.00)))
                    .andExpect(jsonPath("$.rims", hasSize(1)))
                    .andExpect(jsonPath("$.rims[0].name", is("19\" Base Alloy")))
                    .andExpect(jsonPath("$.rims[0].price", is(0)))
                    .andExpect(jsonPath("$.interiors", hasSize(1)))
                    .andExpect(jsonPath("$.interiors[0].name", is("Black Leather")));
        }

        @Test
        @DisplayName("should return 404 when vehicle not found")
        void shouldReturn404WhenVehicleNotFound() throws Exception {
            // Given
            when(configurationService.getConfigurationForVehicle(999))
                    .thenThrow(new VehicleNotFoundException(999));

            // When/Then
            mockMvc.perform(get("/api/v1/vehicles/999/configuration")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status", is(404)))
                    .andExpect(jsonPath("$.message", containsString("999")));
        }
    }
}
