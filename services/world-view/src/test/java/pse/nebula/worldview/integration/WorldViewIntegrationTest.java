package pse.nebula.worldview.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import pse.nebula.worldview.infrastructure.adapter.web.dto.JourneyStateDto;
import pse.nebula.worldview.infrastructure.adapter.web.dto.RouteDto;
import pse.nebula.worldview.infrastructure.adapter.web.dto.StartJourneyRequest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the World View Service.
 * Tests the complete flow from REST API to database and back.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("World View Integration Tests")
class WorldViewIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/v1";
    }

    @Nested
    @DisplayName("Route API Integration Tests")
    class RouteApiTests {

        @Test
        @DisplayName("Should return all 8 predefined routes")
        void shouldReturnAll8Routes() {
            // When
            ResponseEntity<RouteDto[]> response = restTemplate.getForEntity(
                    baseUrl + "/routes",
                    RouteDto[].class
            );

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(8, response.getBody().length);

            // Verify all routes have valid structure
            for (RouteDto route : response.getBody()) {
                assertNotNull(route.getId());
                assertNotNull(route.getName());
                assertNotNull(route.getStartPoint());
                assertNotNull(route.getEndPoint());
                assertNotNull(route.getWaypoints());
                assertTrue(route.getWaypoints().size() >= 2);
                assertTrue(route.getTotalDistanceMeters() > 0);
                assertTrue(route.getEstimatedDurationSeconds() > 0);
            }
        }

        @Test
        @DisplayName("All routes should end at Porsche Zentrum Stuttgart")
        void allRoutesShouldEndAtDealership() {
            // Given
            double dealershipLat = 48.8354;
            double dealershipLng = 9.1520;

            // When
            ResponseEntity<RouteDto[]> response = restTemplate.getForEntity(
                    baseUrl + "/routes",
                    RouteDto[].class
            );

            // Then
            assertNotNull(response.getBody());
            for (RouteDto route : response.getBody()) {
                assertEquals(dealershipLat, route.getEndPoint().getLatitude(), 0.0001,
                        "Route " + route.getId() + " should end at dealership latitude");
                assertEquals(dealershipLng, route.getEndPoint().getLongitude(), 0.0001,
                        "Route " + route.getId() + " should end at dealership longitude");
            }
        }

        @Test
        @DisplayName("Routes should start from different Stuttgart area locations")
        void routesShouldStartFromDifferentLocations() {
            // When
            ResponseEntity<RouteDto[]> response = restTemplate.getForEntity(
                    baseUrl + "/routes",
                    RouteDto[].class
            );

            // Then
            assertNotNull(response.getBody());

            // Verify routes have different starting points
            long uniqueStartPoints = java.util.Arrays.stream(response.getBody())
                    .map(r -> r.getStartPoint().getLatitude() + "," + r.getStartPoint().getLongitude())
                    .distinct()
                    .count();

            assertEquals(8, uniqueStartPoints, "All 8 routes should have unique starting points");
        }

        @Test
        @DisplayName("Should get specific route by ID")
        void shouldGetRouteById() {
            // When
            ResponseEntity<RouteDto> response = restTemplate.getForEntity(
                    baseUrl + "/routes/route-1",
                    RouteDto.class
            );

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("route-1", response.getBody().getId());
            assertEquals("Ludwigsburg Schloss Route", response.getBody().getName());
        }

        @Test
        @DisplayName("Should return 404 for non-existent route")
        void shouldReturn404ForNonExistentRoute() {
            // When
            ResponseEntity<String> response = restTemplate.getForEntity(
                    baseUrl + "/routes/non-existent",
                    String.class
            );

            // Then
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }

        @Test
        @DisplayName("Should get random route")
        void shouldGetRandomRoute() {
            // When
            ResponseEntity<RouteDto> response = restTemplate.getForEntity(
                    baseUrl + "/routes/random",
                    RouteDto.class
            );

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertNotNull(response.getBody().getId());
            assertTrue(response.getBody().getId().startsWith("route-"));
        }

        @Test
        @DisplayName("Should get route count")
        void shouldGetRouteCount() {
            // When
            ResponseEntity<Integer> response = restTemplate.getForEntity(
                    baseUrl + "/routes/count",
                    Integer.class
            );

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(8, response.getBody());
        }
    }

    @Nested
    @DisplayName("Journey API Integration Tests")
    class JourneyApiTests {

        @Test
        @DisplayName("Should start a new journey with random route")
        void shouldStartNewJourneyWithRandomRoute() {
            // Given
            String journeyId = UUID.randomUUID().toString();
            StartJourneyRequest request = new StartJourneyRequest();
            request.setJourneyId(journeyId);
            request.setSpeedMetersPerSecond(13.89);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<StartJourneyRequest> entity = new HttpEntity<>(request, headers);

            // When
            ResponseEntity<JourneyStateDto> response = restTemplate.postForEntity(
                    baseUrl + "/journeys",
                    entity,
                    JourneyStateDto.class
            );

            // Then
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(journeyId, response.getBody().getJourneyId());
            assertEquals("IN_PROGRESS", response.getBody().getStatus());
            assertNotNull(response.getBody().getRoute());
            assertNotNull(response.getBody().getCurrentPosition());
        }

        @Test
        @DisplayName("Should start journey on specific route")
        void shouldStartJourneyOnSpecificRoute() {
            // Given
            String journeyId = UUID.randomUUID().toString();
            StartJourneyRequest request = new StartJourneyRequest();
            request.setJourneyId(journeyId);
            request.setRouteId("route-3");
            request.setSpeedMetersPerSecond(20.0);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<StartJourneyRequest> entity = new HttpEntity<>(request, headers);

            // When
            ResponseEntity<JourneyStateDto> response = restTemplate.postForEntity(
                    baseUrl + "/journeys",
                    entity,
                    JourneyStateDto.class
            );

            // Then
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("route-3", response.getBody().getRoute().getId());
            assertEquals("Kornwestheim Route", response.getBody().getRoute().getName());
        }

        @Test
        @DisplayName("Should get journey state")
        void shouldGetJourneyState() {
            // Given - Start a journey first
            String journeyId = UUID.randomUUID().toString();
            StartJourneyRequest request = new StartJourneyRequest();
            request.setJourneyId(journeyId);
            request.setSpeedMetersPerSecond(13.89);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            restTemplate.postForEntity(
                    baseUrl + "/journeys",
                    new HttpEntity<>(request, headers),
                    JourneyStateDto.class
            );

            // When
            ResponseEntity<JourneyStateDto> response = restTemplate.getForEntity(
                    baseUrl + "/journeys/" + journeyId,
                    JourneyStateDto.class
            );

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(journeyId, response.getBody().getJourneyId());
        }

        @Test
        @DisplayName("Should return 404 for non-existent journey")
        void shouldReturn404ForNonExistentJourney() {
            // When
            ResponseEntity<String> response = restTemplate.getForEntity(
                    baseUrl + "/journeys/non-existent-journey",
                    String.class
            );

            // Then
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }

        @Test
        @DisplayName("Should pause and resume journey")
        void shouldPauseAndResumeJourney() {
            // Given - Start a journey first
            String journeyId = UUID.randomUUID().toString();
            StartJourneyRequest request = new StartJourneyRequest();
            request.setJourneyId(journeyId);
            request.setSpeedMetersPerSecond(13.89);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            restTemplate.postForEntity(
                    baseUrl + "/journeys",
                    new HttpEntity<>(request, headers),
                    JourneyStateDto.class
            );

            // When - Pause the journey
            ResponseEntity<JourneyStateDto> pauseResponse = restTemplate.postForEntity(
                    baseUrl + "/journeys/" + journeyId + "/pause",
                    null,
                    JourneyStateDto.class
            );

            // Then
            assertEquals(HttpStatus.OK, pauseResponse.getStatusCode());
            assertEquals("PAUSED", pauseResponse.getBody().getStatus());

            // When - Resume the journey
            ResponseEntity<JourneyStateDto> resumeResponse = restTemplate.postForEntity(
                    baseUrl + "/journeys/" + journeyId + "/resume",
                    null,
                    JourneyStateDto.class
            );

            // Then
            assertEquals(HttpStatus.OK, resumeResponse.getStatusCode());
            assertEquals("IN_PROGRESS", resumeResponse.getBody().getStatus());
        }

        @Test
        @DisplayName("Should stop journey")
        void shouldStopJourney() {
            // Given - Start a journey first
            String journeyId = UUID.randomUUID().toString();
            StartJourneyRequest request = new StartJourneyRequest();
            request.setJourneyId(journeyId);
            request.setSpeedMetersPerSecond(13.89);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            restTemplate.postForEntity(
                    baseUrl + "/journeys",
                    new HttpEntity<>(request, headers),
                    JourneyStateDto.class
            );

            // When - Stop the journey
            restTemplate.delete(baseUrl + "/journeys/" + journeyId);

            // Then - Journey should no longer exist
            ResponseEntity<String> response = restTemplate.getForEntity(
                    baseUrl + "/journeys/" + journeyId,
                    String.class
            );
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }

        @Test
        @DisplayName("Should fail to start duplicate journey")
        void shouldFailToStartDuplicateJourney() {
            // Given - Start a journey first
            String journeyId = UUID.randomUUID().toString();
            StartJourneyRequest request = new StartJourneyRequest();
            request.setJourneyId(journeyId);
            request.setSpeedMetersPerSecond(13.89);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<StartJourneyRequest> entity = new HttpEntity<>(request, headers);

            restTemplate.postForEntity(baseUrl + "/journeys", entity, JourneyStateDto.class);

            // When - Try to start another journey with same ID
            ResponseEntity<String> response = restTemplate.postForEntity(
                    baseUrl + "/journeys",
                    entity,
                    String.class
            );

            // Then
            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("Route Data Validation Tests")
    class RouteDataValidationTests {

        @Test
        @DisplayName("Route 1 should be from Ludwigsburg Schloss")
        void route1ShouldBeFromLudwigsburg() {
            ResponseEntity<RouteDto> response = restTemplate.getForEntity(
                    baseUrl + "/routes/route-1",
                    RouteDto.class
            );

            assertNotNull(response.getBody());
            assertEquals("Ludwigsburg Schloss Route", response.getBody().getName());
            // Ludwigsburg Schloss coordinates
            assertEquals(48.8973, response.getBody().getStartPoint().getLatitude(), 0.001);
            assertEquals(9.1920, response.getBody().getStartPoint().getLongitude(), 0.001);
        }

        @Test
        @DisplayName("Route 2 should be from Favoritepark")
        void route2ShouldBeFromFavoritepark() {
            ResponseEntity<RouteDto> response = restTemplate.getForEntity(
                    baseUrl + "/routes/route-2",
                    RouteDto.class
            );

            assertNotNull(response.getBody());
            assertEquals("Favoritepark Route", response.getBody().getName());
        }

        @Test
        @DisplayName("Route waypoints should form a continuous path")
        void routeWaypointsShouldFormContinuousPath() {
            ResponseEntity<RouteDto[]> response = restTemplate.getForEntity(
                    baseUrl + "/routes",
                    RouteDto[].class
            );

            assertNotNull(response.getBody());
            for (RouteDto route : response.getBody()) {
                var waypoints = route.getWaypoints();

                // First waypoint should match start point
                assertEquals(route.getStartPoint().getLatitude(),
                        waypoints.get(0).getLatitude(), 0.0001);
                assertEquals(route.getStartPoint().getLongitude(),
                        waypoints.get(0).getLongitude(), 0.0001);

                // Last waypoint should match end point
                var lastWaypoint = waypoints.get(waypoints.size() - 1);
                assertEquals(route.getEndPoint().getLatitude(),
                        lastWaypoint.getLatitude(), 0.0001);
                assertEquals(route.getEndPoint().getLongitude(),
                        lastWaypoint.getLongitude(), 0.0001);
            }
        }
    }
}

