package pse.nebula.user.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class TokenBlacklistService {

    private static final Logger log = LoggerFactory.getLogger(TokenBlacklistService.class);
    private static final String BLACKLIST_PREFIX = "blacklist:";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.test-public-key}")
    private String publicKeyPem;

    private PublicKey publicKey;

    /**
     * Add token to blacklist (called on logout)
     * Token is stored with TTL = time until expiration
     */
    public void blacklistToken(String token) {
        try {
            // Parse token to get expiration time
            Claims claims = parseToken(token);
            Date expiration = claims.getExpiration();
            
            // Calculate seconds until expiration
            long now = System.currentTimeMillis();
            long expirationTime = expiration.getTime();
            long secondsUntilExpiry = (expirationTime - now) / 1000;

            if (secondsUntilExpiry <= 0) {
                log.warn("Attempted to blacklist already expired token");
                return;
            }

            // Store in Redis with TTL
            String key = BLACKLIST_PREFIX + token;
            redisTemplate.opsForValue().set(key, "revoked", secondsUntilExpiry, TimeUnit.SECONDS);
            
            log.info("Token blacklisted for user: {} (expires in {} seconds)", 
                     claims.getSubject(), secondsUntilExpiry);

        } catch (Exception e) {
            log.error("Failed to blacklist token: {}", e.getMessage());
            throw new RuntimeException("Failed to blacklist token", e);
        }
    }

    /**
     * Check if token is blacklisted
     */
    public boolean isBlacklisted(String token) {
        try {
            String key = BLACKLIST_PREFIX + token;
            Boolean exists = redisTemplate.hasKey(key);
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            log.error("Failed to check blacklist: {}", e.getMessage());
            // Fail-safe: if Redis is down, don't block requests
            return false;
        }
    }

    /**
     * Parse token to extract claims (for getting expiration)
     */
    private Claims parseToken(String token) {
        try {
            PublicKey key = getPublicKey();
            
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
                    
        } catch (Exception e) {
            log.error("Failed to parse token: {}", e.getMessage());
            throw new RuntimeException("Invalid token", e);
        }
    }

    /**
     * Load public key from PEM format
     */
    private PublicKey getPublicKey() {
        if (publicKey != null) {
            return publicKey;
        }

        try {
            String publicKeyPEM = publicKeyPem
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
            publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (Exception e) {
            log.error("Failed to load public key: {}", e.getMessage());
            throw new RuntimeException("Failed to load public key", e);
        }
    }
}
