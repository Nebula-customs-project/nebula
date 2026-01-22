package pse.nebula.user.service;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

/**
 * Redis-based token blacklist service
 * Stores blacklisted tokens in Redis with automatic TTL expiration
 */
@Service
@Slf4j
public class RedisTokenBlacklistService {

    private static final String BLACKLIST_KEY_PREFIX = "blacklist:";
    
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtUtil jwtUtil;

    public RedisTokenBlacklistService(RedisTemplate<String, String> redisTemplate, JwtUtil jwtUtil) {
        this.redisTemplate = redisTemplate;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Blacklist a token with automatic TTL expiration
     * 
     * @param token JWT token to blacklist
     * @param userId User ID who owns the token
     * @param reason Reason for blacklisting (e.g., "LOGOUT", "SECURITY_BREACH")
     */
    public void blacklistToken(String token, String userId, String reason) {
        try {
            // Parse token to get expiration
            Claims claims = jwtUtil.parseToken(token);
            Date expiration = claims.getExpiration();
            
            // Calculate TTL in seconds
            long ttlSeconds = calculateTTL(expiration);
            
            if (ttlSeconds <= 0) {
                log.warn("Token already expired, not adding to blacklist");
                return;
            }
            
            // Hash the token for security
            String tokenHash = hashToken(token);
            String redisKey = BLACKLIST_KEY_PREFIX + tokenHash;
            
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
            
            log.info("Token blacklisted for user: {}, reason: {}, TTL: {}s", userId, reason, ttlSeconds);
        } catch (Exception e) {
            log.error("Error blacklisting token", e);
            throw new IllegalStateException("Failed to blacklist token", e);
        }
    }

    /**
     * Check if a token is blacklisted
     * 
     * @param token JWT token to check
     * @return true if blacklisted, false otherwise
     */
    public boolean isBlacklisted(String token) {
        try {
            String tokenHash = hashToken(token);
            String redisKey = BLACKLIST_KEY_PREFIX + tokenHash;
            
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
            String tokenHash = hashToken(token);
            String redisKey = BLACKLIST_KEY_PREFIX + tokenHash;
            
            String value = redisTemplate.opsForValue().get(redisKey);
            
            if (value == null) {
                return Optional.empty();
            }
            
            // Parse simple string: userId:reason:timestamp
            String[] parts = value.split(":", 3);
            if (parts.length == 3) {
                return Optional.of(new BlacklistInfo(parts[0], parts[1], parts[2]));
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
     * Hash token using SHA-256 for security
     * Prevents storing raw JWT tokens in Redis
     */
    private String hashToken(String token) {
        return DigestUtils.sha256Hex(token);
    }

    /**
     * Simple data class for blacklist information
     */
    public record BlacklistInfo(String userId, String reason, String timestamp) {}
}
