package pse.nebula.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtValidator {

    private static final Logger log = LoggerFactory.getLogger(JwtValidator.class);

    @Value("${jwt.use-hardcoded-key:true}")
    private boolean useHardcodedKey;

    @Value("${jwt.test-public-key}")
    private String testPublicKeyPem;

    private PublicKey publicKey;

    /**
     * Validate JWT token and extract claims
     */
    public Claims validateToken(String token) {
        try {
            PublicKey key = getPublicKey();

            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // Check expiry
            if (claims.getExpiration().before(new Date())) {
                log.warn("Token expired at: {}", claims.getExpiration());
                throw new RuntimeException("Token expired");
            }

            log.debug("Token validated successfully for user: {}", claims.getSubject());
            return claims;

        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
            throw new RuntimeException("Invalid token signature");
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            throw new RuntimeException("Token validation failed: " + e.getMessage());
        }
    }

    /**
     * Get public key (hardcoded for testing, JWKS in production)
     */
    private PublicKey getPublicKey() {
        if (publicKey != null) {
            return publicKey;
        }

        try {
            if (useHardcodedKey) {
                log.info("Using hardcoded test public key");
                publicKey = loadPublicKeyFromPem(testPublicKeyPem);
            } else {
                // TODO: Fetch from JWKS endpoint when user service is ready
                log.info("Fetching public key from JWKS endpoint");
                throw new UnsupportedOperationException("JWKS fetching not implemented yet");
            }
            return publicKey;
        } catch (Exception e) {
            log.error("Failed to load public key: {}", e.getMessage());
            throw new RuntimeException("Failed to load public key", e);
        }
    }

    /**
     * Load RSA public key from PEM format
     */
    private PublicKey loadPublicKeyFromPem(String pemKey) throws Exception {
        String publicKeyPEM = pemKey
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * Extract user ID from token
     */
    public String getUserId(Claims claims) {
        return claims.getSubject();
    }

    /**
     * Extract email from token
     */
    public String getEmail(Claims claims) {
        return claims.get("email", String.class);
    }

    /**
     * Extract roles from token (optional)
     */
    public String getRoles(Claims claims) {
        Object roles = claims.get("roles");
        return roles != null ? roles.toString() : "";
    }
}
