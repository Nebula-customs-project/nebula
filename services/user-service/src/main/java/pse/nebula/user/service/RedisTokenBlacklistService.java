package pse.nebula.user.service;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

/**
 * Redis-based token blacklist service using JTI (JWT ID) approach.
 * 
 * Instead of storing the full token or its hash, we store only the JTI (UUID)
 * which is a 36-character unique identifier embedded in each JWT token.
 * 
 * Benefits of JTI approach:
 * - Smaller storage: 36 chars (JTI) vs 64 chars (hash) vs 500+ chars (full token)
 * - No hashing needed: JTI is already unique and non-sensitive
 * - Automatic TTL: Redis auto-expires entries when token would have expired
 */
@Service
@Slf4j
public class RedisTokenBlacklistService {

    private static final String BLACKLIST_KEY_PREFIX = "blacklist:jti:";
    
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtUtil jwtUtil;

    public RedisTokenBlacklistService(RedisTemplate<String, String> redisTemplate, JwtUtil jwtUtil) {
        this.redisTemplate = redisTemplate;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Blacklist a token using its JTI (JWT ID) with automatic TTL expiration.
     * 
     * @param token JWT token to blacklist
     * @param userId User ID who owns the token
     * @param reason Reason for blacklisting (e.g., "LOGOUT", "SECURITY_BREACH")
     */
    public void blacklistToken(String token, String userId, String reason) {
        try {
            // Parse token to get JTI and expiration
            Claims claims = jwtUtil.parseToken(token);
            String jti = claims.getId();
            Date expiration = claims.getExpiration();
            
            if (jti == null || jti.isBlank()) {
                log.error("Token has no JTI claim, cannot blacklist");
                throw new IllegalArgumentException("Token missing JTI claim");
            }
            
            // Calculate TTL in seconds
            long ttlSeconds = calculateTTL(expiration);
            
            if (ttlSeconds <= 0) {
                log.warn("Token already expired, not adding to blacklist");
                return;
            }
            
            // Use JTI directly as the key (no hashing needed - JTI is already a UUID)
            String redisKey = BLACKLIST_KEY_PREFIX + jti;
            
            // Create simple string value: userId:reason:timestamp
            String value = String.format("%s:%s:%s", 
                userId, 
                reason, 
                Instant.now().toString()
            );
            
            // Store in Redis with TTL - Redis auto-expires after token expiration
            redisTemplate.opsForValue().set(
                redisKey,
                value,
                Duration.ofSeconds(ttlSeconds)
            );
            
            log.info("Token blacklisted - JTI: {}, user: {}, reason: {}, TTL: {}s", 
                     jti, userId, reason, ttlSeconds);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error blacklisting token", e);
            throw new IllegalStateException("Failed to blacklist token", e);
        }
    }

    /**
     * Check if a token is blacklisted by looking up its JTI.
     * 
     * @param token JWT token to check
     * @return true if blacklisted, false otherwise
     */
    public boolean isBlacklisted(String token) {
        try {
            // Extract JTI from token
            String jti = jwtUtil.getTokenId(token);
            
            if (jti == null || jti.isBlank()) {
                log.warn("Token has no JTI claim, cannot check blacklist");
                return false;
            }
            
            String redisKey = BLACKLIST_KEY_PREFIX + jti;
            
            // Check if key exists in Redis
            Boolean exists = redisTemplate.hasKey(redisKey);
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            log.error("Error checking blacklist", e);
            // Fail-safe: On error, assume not blacklisted to avoid blocking legitimate requests
            return false;
        }
    }

    /**
     * Get blacklist details for a token (optional, for logging/debugging)
     * 
     * @param token JWT token
     * @return Optional containing blacklist info if found
     */
    public Optional<BlacklistInfo> getBlacklistDetails(String token) {
        try {
            String jti = jwtUtil.getTokenId(token);
            
            if (jti == null || jti.isBlank()) {
                return Optional.empty();
            }
            
            String redisKey = BLACKLIST_KEY_PREFIX + jti;
            String value = redisTemplate.opsForValue().get(redisKey);
            
            if (value == null) {
                return Optional.empty();
            }
            
            // Parse simple string: userId:reason:timestamp
            String[] parts = value.split(":", 3);
            if (parts.length == 3) {
                return Optional.of(new BlacklistInfo(jti, parts[0], parts[1], parts[2]));
            }
            
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error getting blacklist details", e);
            return Optional.empty();
        }
    }

    /**
     * Calculate TTL in seconds until token expiration
     */
    private long calculateTTL(Date expiration) {
        long expirationTime = expiration.getTime();
        long currentTime = System.currentTimeMillis();
        long ttlMillis = expirationTime - currentTime;
        
        // Add 60 second buffer to handle clock skew
        return (ttlMillis / 1000) + 60;
    }

    /**
     * Data class for blacklist information
     */
    public record BlacklistInfo(String jti, String userId, String reason, String timestamp) {}
}
