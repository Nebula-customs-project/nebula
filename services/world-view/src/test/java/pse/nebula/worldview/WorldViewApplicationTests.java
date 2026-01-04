package pse.nebula.worldview;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import pse.nebula.worldview.infrastructure.adapter.web.dto.RouteDto;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class WorldViewApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void contextLoads() {
        // Context loads successfully
    }

    @Test
    void getAllRoutes_shouldReturn8Routes() {
        ResponseEntity<RouteDto[]> response = restTemplate.getForEntity(
            "http://localhost:" + port + "/api/v1/routes",
            RouteDto[].class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(8, response.getBody().length);
    }

    @Test
    void getRouteById_shouldReturnRoute() {
        ResponseEntity<RouteDto> response = restTemplate.getForEntity(
            "http://localhost:" + port + "/api/v1/routes/route-1",
            RouteDto.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("route-1", response.getBody().getId());
        assertEquals("Ludwigsburg Schloss Route", response.getBody().getName());
    }

    @Test
    void getRandomRoute_shouldReturnARoute() {
        ResponseEntity<RouteDto> response = restTemplate.getForEntity(
            "http://localhost:" + port + "/api/v1/routes/random",
            RouteDto.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        // All routes end at the dealership
        assertEquals(48.8354, response.getBody().getEndPoint().getLatitude(), 0.0001);
        assertEquals(9.152, response.getBody().getEndPoint().getLongitude(), 0.0001);
    }

    @Test
    void getRouteCount_shouldReturn8() {
        ResponseEntity<Integer> response = restTemplate.getForEntity(
            "http://localhost:" + port + "/api/v1/routes/count",
            Integer.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(8, response.getBody());
    }

    @Test
    void allRoutes_shouldEndAtDealership() {
        ResponseEntity<RouteDto[]> response = restTemplate.getForEntity(
            "http://localhost:" + port + "/api/v1/routes",
            RouteDto[].class
        );

        assertNotNull(response.getBody());
        for (RouteDto route : response.getBody()) {
            // All routes should end at Dealership
            assertEquals(48.8354, route.getEndPoint().getLatitude(), 0.0001,
                "Route " + route.getId() + " should end at dealership latitude");
            assertEquals(9.152, route.getEndPoint().getLongitude(), 0.0001,
                "Route " + route.getId() + " should end at dealership longitude");
        }
    }
}

