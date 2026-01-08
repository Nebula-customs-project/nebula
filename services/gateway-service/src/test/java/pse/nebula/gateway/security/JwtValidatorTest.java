package pse.nebula.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for JwtValidator
 */
class JwtValidatorTest {

    private JwtValidator jwtValidator;
    private KeyPair keyPair;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    @BeforeEach
    void setUp() throws Exception {
        jwtValidator = new JwtValidator();

        // Generate a test RSA key pair
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        keyPair = keyGen.generateKeyPair();
        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();

        // Convert public key to PEM format
        String publicKeyPem = "-----BEGIN PUBLIC KEY-----\n" +
                Base64.getEncoder().encodeToString(publicKey.getEncoded()) +
                "\n-----END PUBLIC KEY-----";

        // Set the test configuration
        ReflectionTestUtils.setField(jwtValidator, "useHardcodedKey", true);
        ReflectionTestUtils.setField(jwtValidator, "testPublicKeyPem", publicKeyPem);
    }

    @Test
    void testValidateToken_ValidToken_Success() {
        // Given: A valid JWT token
        String token = createValidToken("test-user-123", "test@example.com", "USER", 3600000);

        // When: Validating the token
        Claims claims = jwtValidator.validateToken(token);

        // Then: Claims should be extracted successfully
        assertNotNull(claims);
        assertEquals("test-user-123", claims.getSubject());
        assertEquals("test@example.com", claims.get("email", String.class));
        assertEquals("USER", claims.get("roles", String.class));
    }

    @Test
    void testValidateToken_ExpiredToken_ThrowsException() {
        // Given: An expired token (expired 1 hour ago)
        String token = createValidToken("test-user-123", "test@example.com", "USER", -3600000);

        // When & Then: Should throw exception for expired token
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> jwtValidator.validateToken(token));
        assertTrue(exception.getMessage().contains("Token expired") ||
                   exception.getMessage().contains("Token validation failed"));
    }

    @Test
    void testValidateToken_InvalidSignature_ThrowsException() {
        // Given: A token signed with a different key
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair wrongKeyPair = keyGen.generateKeyPair();

            String token = Jwts.builder()
                    .subject("test-user-123")
                    .claim("email", "test@example.com")
                    .claim("roles", "USER")
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + 3600000))
                    .signWith(wrongKeyPair.getPrivate())
                    .compact();

            // When & Then: Should throw exception for invalid signature
            assertThrows(RuntimeException.class, () -> jwtValidator.validateToken(token));
        } catch (Exception e) {
            fail("Setup failed: " + e.getMessage());
        }
    }

    @Test
    void testValidateToken_MalformedToken_ThrowsException() {
        // Given: A malformed token
        String malformedToken = "not.a.valid.jwt.token";

        // When & Then: Should throw exception for malformed token
        assertThrows(RuntimeException.class, () -> jwtValidator.validateToken(malformedToken));
    }

    @Test
    void testValidateToken_EmptyToken_ThrowsException() {
        // Given: An empty token
        String emptyToken = "";

        // When & Then: Should throw exception for empty token
        assertThrows(RuntimeException.class, () -> jwtValidator.validateToken(emptyToken));
    }

    @Test
    void testValidateToken_NullToken_ThrowsException() {
        // Given: A null token
        String nullToken = null;

        // When & Then: Should throw exception for null token
        assertThrows(RuntimeException.class, () -> jwtValidator.validateToken(nullToken));
    }

    @Test
    void testGetUserId_ValidClaims_ReturnsUserId() {
        // Given: Valid claims with subject
        String token = createValidToken("user-456", "user@test.com", "ADMIN", 3600000);
        Claims claims = jwtValidator.validateToken(token);

        // When: Getting user ID
        String userId = jwtValidator.getUserId(claims);

        // Then: Should return correct user ID
        assertEquals("user-456", userId);
    }

    @Test
    void testGetEmail_ValidClaims_ReturnsEmail() {
        // Given: Valid claims with email
        String token = createValidToken("user-789", "email@domain.com", "USER", 3600000);
        Claims claims = jwtValidator.validateToken(token);

        // When: Getting email
        String email = jwtValidator.getEmail(claims);

        // Then: Should return correct email
        assertEquals("email@domain.com", email);
    }

    @Test
    void testGetRoles_ValidClaims_ReturnsRoles() {
        // Given: Valid claims with roles
        String token = createValidToken("user-101", "admin@test.com", "ADMIN,USER", 3600000);
        Claims claims = jwtValidator.validateToken(token);

        // When: Getting roles
        String roles = jwtValidator.getRoles(claims);

        // Then: Should return correct roles
        assertEquals("ADMIN,USER", roles);
    }

    @Test
    void testGetRoles_NoRoles_ReturnsEmptyString() {
        // Given: Valid claims without roles
        String token = createTokenWithoutRoles("user-102", "user@test.com", 3600000);
        Claims claims = jwtValidator.validateToken(token);

        // When: Getting roles
        String roles = jwtValidator.getRoles(claims);

        // Then: Should return empty string
        assertEquals("", roles);
    }

    @Test
    void testGetEmail_NoEmail_ReturnsNull() {
        // Given: Valid claims without email
        String token = createTokenWithoutEmail("user-103", "ADMIN", 3600000);
        Claims claims = jwtValidator.validateToken(token);

        // When: Getting email
        String email = jwtValidator.getEmail(claims);

        // Then: Should return null
        assertNull(email);
    }

    @Test
    void testValidateToken_TokenAboutToExpire_Success() {
        // Given: A token that expires in 1 second
        String token = createValidToken("user-104", "test@example.com", "USER", 1000);

        // When: Validating the token
        Claims claims = jwtValidator.validateToken(token);

        // Then: Should still be valid
        assertNotNull(claims);
        assertEquals("user-104", claims.getSubject());
    }

    @Test
    void testValidateToken_LongLivedToken_Success() {
        // Given: A token valid for 24 hours
        String token = createValidToken("user-105", "test@example.com", "USER", 86400000);

        // When: Validating the token
        Claims claims = jwtValidator.validateToken(token);

        // Then: Should be valid
        assertNotNull(claims);
        assertEquals("user-105", claims.getSubject());
    }

    @Test
    void testValidateToken_SpecialCharactersInClaims_Success() {
        // Given: A token with special characters in claims
        String token = createValidToken("user@#$%", "test+special@example.com", "USER,ADMIN", 3600000);

        // When: Validating the token
        Claims claims = jwtValidator.validateToken(token);

        // Then: Should handle special characters
        assertNotNull(claims);
        assertEquals("user@#$%", claims.getSubject());
        assertEquals("test+special@example.com", claims.get("email"));
    }

    // Helper methods

    private String createValidToken(String subject, String email, String roles, long expirationMillis) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .subject(subject)
                .claim("email", email)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(privateKey)
                .compact();
    }

    private String createTokenWithoutRoles(String subject, String email, long expirationMillis) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .subject(subject)
                .claim("email", email)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(privateKey)
                .compact();
    }

    private String createTokenWithoutEmail(String subject, String roles, long expirationMillis) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .subject(subject)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(privateKey)
                .compact();
    }
}

