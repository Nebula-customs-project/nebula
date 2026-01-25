package pse.nebula.uservehicleservice.infrastructure.adapter.inbound.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pse.nebula.uservehicleservice.application.service.UserVehicleAssignmentService;
import pse.nebula.uservehicleservice.domain.model.UserVehicle;
import pse.nebula.uservehicleservice.infrastructure.adapter.inbound.rest.dto.ErrorResponse;
import pse.nebula.uservehicleservice.infrastructure.adapter.inbound.rest.dto.UserVehicleInfoResponse;
import pse.nebula.uservehicleservice.infrastructure.adapter.outbound.rest.VehicleServiceClient;

/**
 * REST controller for user vehicle operations.
 * Provides endpoint for retrieving user's vehicle information.
 * Real-time telemetry updates are handled via WebSocket connection.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/user-vehicle")
@Tag(name = "User Vehicle", description = "User vehicle information operations")
public class UserVehicleController {

        private static final String USER_ID_HEADER = "X-User-Id";

        private final UserVehicleAssignmentService assignmentService;
        private final VehicleServiceClient vehicleServiceClient;

        public UserVehicleController(UserVehicleAssignmentService assignmentService,
                        VehicleServiceClient vehicleServiceClient) {
                this.assignmentService = assignmentService;
                this.vehicleServiceClient = vehicleServiceClient;
        }

        /**
         * Gets user's vehicle information including vehicle name, image, maintenance
         * date and tyre
         * pressures.
         * On first call, assigns a random vehicle to the user.
         * For real-time telemetry updates, connect via WebSocket at
         * /ws/vehicle-telemetry.
         *
         * @param userId the user ID from request header (validated by gateway)
         * @return user's vehicle information including name, image, maintenance and
         *         tyre pressures
         */
        @GetMapping("/info")
        @Operation(summary = "Get user vehicle info", description = "Returns vehicle name, image, maintenance date and tyre pressures for user's vehicle. "
                        +
                        "Assigns a random vehicle on first call. " +
                        "For real-time telemetry, connect via WebSocket at /ws/vehicle-telemetry.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved vehicle info", content = @Content(schema = @Schema(implementation = UserVehicleInfoResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Missing or invalid user ID header", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "503", description = "Vehicle service unavailable", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<UserVehicleInfoResponse> getUserVehicleInfo(
                        @Parameter(description = "User ID", required = true) @RequestHeader(USER_ID_HEADER) String userId) {

                log.info("Received request for user vehicle info. UserId: {}", userId);

                // Get or assign vehicle for the user
                UserVehicle userVehicle = assignmentService.getOrAssignVehicle(userId);

                // Fetch vehicle image from vehicle-service
                String vehicleImage = vehicleServiceClient.getVehicleById(userVehicle.getVehicleId())
                                .map(vehicle -> vehicle.image())
                                .orElse(null);

                // Build and return response with vehicle image
                UserVehicleInfoResponse response = UserVehicleInfoResponse.fromEntity(userVehicle, vehicleImage);

                log.info("Returning vehicle info for user: {}. Vehicle: {}, MaintenanceDue: {}",
                                userId, response.vehicleName(), response.maintenanceDueDate());

                return ResponseEntity.ok(response);
        }
}
