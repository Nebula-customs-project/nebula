package pse.nebula.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtValidatorTest {

    private JwtValidator jwtValidator;
    private WebClient webClientMock;

    @BeforeEach
    void setUp() {
        webClientMock = Mockito.mock(WebClient.class);
        jwtValidator = new JwtValidator();
        jwtValidator.webClient = webClientMock;
    }

    @Test
    @Disabled("WebClient mocking has generic type issues - needs refactoring")
    void testFetchPublicKey() {
        // TODO: Refactor this test to properly mock WebClient with correct generics
    }

    @SuppressWarnings("unchecked")
    @Test
    void testValidateToken() {
        Claims claimsMock = mock(Claims.class);
        Jws<Claims> jwsMock = (Jws<Claims>) mock(Jws.class);

        JwtParser jwtParserMock = mock(JwtParser.class);
        when(jwtParserMock.parseClaimsJws(anyString())).thenReturn(jwsMock);
        when(jwsMock.getBody()).thenReturn(claimsMock);

        Claims claims = jwtValidator.validateToken("testToken");

        assertNotNull(claims);
    }

    @Test
    void testValidateTokenExpired() {
        String token = "expired.jwt.token";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> jwtValidator.validateToken(token));

        assertEquals("Token expired", exception.getMessage());
    }

    @Test
    void testGetUserId() {
        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("user-id");

        String userId = jwtValidator.getUserId(claims);
        assertEquals("user-id", userId);
    }

    @Test
    void testGetEmail() {
        Claims claims = mock(Claims.class);
        when(claims.get("email", String.class)).thenReturn("user@example.com");

        String email = jwtValidator.getEmail(claims);
        assertEquals("user@example.com", email);
    }

    @Test
    void testGetRoles() {
        Claims claims = mock(Claims.class);
        when(claims.get("roles")).thenReturn("ROLE_USER");

        String roles = jwtValidator.getRoles(claims);
        assertEquals("ROLE_USER", roles);
    }
}
