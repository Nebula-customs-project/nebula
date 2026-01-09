package pse.nebula.worldview.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for OpenApiConfig.
 */
@DisplayName("OpenApiConfig Tests")
class OpenApiConfigTest {

    private OpenApiConfig openApiConfig;

    @BeforeEach
    void setUp() {
        openApiConfig = new OpenApiConfig();
        ReflectionTestUtils.setField(openApiConfig, "serverPort", "8082");
    }

    @Test
    @DisplayName("Should create OpenAPI bean")
    void shouldCreateOpenApiBean() {
        OpenAPI openAPI = openApiConfig.worldViewOpenAPI();

        assertNotNull(openAPI);
    }

    @Test
    @DisplayName("Should have correct API title")
    void shouldHaveCorrectApiTitle() {
        OpenAPI openAPI = openApiConfig.worldViewOpenAPI();

        assertNotNull(openAPI.getInfo());
        assertEquals("World-View Service API", openAPI.getInfo().getTitle());
    }

    @Test
    @DisplayName("Should have correct version")
    void shouldHaveCorrectVersion() {
        OpenAPI openAPI = openApiConfig.worldViewOpenAPI();

        assertNotNull(openAPI.getInfo());
        assertEquals("1.0.0", openAPI.getInfo().getVersion());
    }

    @Test
    @DisplayName("Should have description")
    void shouldHaveDescription() {
        OpenAPI openAPI = openApiConfig.worldViewOpenAPI();

        assertNotNull(openAPI.getInfo());
        assertNotNull(openAPI.getInfo().getDescription());
        assertFalse(openAPI.getInfo().getDescription().isEmpty());
    }

    @Test
    @DisplayName("Should have contact information")
    void shouldHaveContactInformation() {
        OpenAPI openAPI = openApiConfig.worldViewOpenAPI();

        assertNotNull(openAPI.getInfo());
        assertNotNull(openAPI.getInfo().getContact());
        assertEquals("Nebula Team", openAPI.getInfo().getContact().getName());
    }

    @Test
    @DisplayName("Should have license information")
    void shouldHaveLicenseInformation() {
        OpenAPI openAPI = openApiConfig.worldViewOpenAPI();

        assertNotNull(openAPI.getInfo());
        assertNotNull(openAPI.getInfo().getLicense());
        assertEquals("MIT License", openAPI.getInfo().getLicense().getName());
    }

    @Test
    @DisplayName("Should have servers configured")
    void shouldHaveServersConfigured() {
        OpenAPI openAPI = openApiConfig.worldViewOpenAPI();

        assertNotNull(openAPI.getServers());
        assertFalse(openAPI.getServers().isEmpty());
    }

    @Test
    @DisplayName("Should have tags configured")
    void shouldHaveTagsConfigured() {
        OpenAPI openAPI = openApiConfig.worldViewOpenAPI();

        assertNotNull(openAPI.getTags());
        assertFalse(openAPI.getTags().isEmpty());
    }
}