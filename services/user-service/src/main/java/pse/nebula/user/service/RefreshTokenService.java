package pse.nebula.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Set;
import java.util.UUID;

/**
 * Service for managing refresh tokens with secure storage and rotation.
 * 
 * Features:
 * - SHA-256 hashed token storage (never stores plain tokens)
 * - Token rotation on each refresh (issues new refresh token)
 * - Reuse detection (revokes entire token family if old token is reused)
 * - Automatic TTL expiration via Redis
 * 
 * Redis Key Structure:
 * - refresh_token:{userId}:{familyId} -> hash:createdAt:lastUsedJti:used
 * - refresh_family:{userId} -> Set of all family IDs for this user
 */
@Service
@Slf4j
public class RefreshTokenService {

    // TODO (Production): Change to 604800 seconds (7 days)
    private static final long REFRESH_TOKEN_TTL_SECONDS = 300; // 5 minutes for testing

    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    private static final String REFRESH_FAMILY_PREFIX = "refresh_family:";
    private static final int TOKEN_BYTES = 32; // 256 bits of randomness

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtUtil jwtUtil;
    private final RedisTokenBlacklistService redisTokenBlacklistService;
    private final SecureRandom secureRandom;

    public RefreshTokenService(RedisTemplate<String, String> redisTemplate, JwtUtil jwtUtil,
            RedisTokenBlacklistService redisTokenBlacklistService) {
        this.redisTemplate = redisTemplate;
        this.jwtUtil = jwtUtil;
        this.redisTokenBlacklistService = redisTokenBlacklistService;
        this.secureRandom = new SecureRandom();
    }

    /**
     * Generates a new refresh token for a user (used during login).
     * Creates a new token family.
     *
     * @param userId User's ID
     * @param email  User's email
     * @param role   User's role
     * @return RefreshTokenData containing token and family info
     */
    public RefreshTokenData generateRefreshToken(String userId, String email, String role) {
        String familyId = UUID.randomUUID().toString();
        return generateTokenForFamily(userId, email, role, familyId);
    }

    /**
     * Validates a refresh token and rotates it (issues new tokens).
     * Implements reuse detection - if token was already used, revokes entire
     * family.
     * Also blacklists the old access token if provided.
     *
     * @param refreshToken   The refresh token to validate
     * @param oldAccessToken Optional old access token to blacklist
     * @return TokenPair containing new access and refresh tokens
     * @throws RefreshTokenException if token is invalid, expired, or reused
     */
    public TokenPair validateAndRotate(String refreshToken, String oldAccessToken) {
        try {
            // Parse token to extract metadata
            RefreshTokenPayload payload = parseRefreshToken(refreshToken);

            String redisKey = REFRESH_TOKEN_PREFIX + payload.userId() + ":" + payload.familyId();
            String storedValue = redisTemplate.opsForValue().get(redisKey);

            if (storedValue == null) {
                log.warn("Refresh token not found in Redis for user: {}", payload.userId());
                throw new RefreshTokenException("Refresh token not found or expired");
            }

            // Parse stored value: hash:createdAt:email:role:used
            String[] parts = storedValue.split(":", 5);
            if (parts.length < 5) {
                log.error("Malformed refresh token data in Redis");
                throw new RefreshTokenException("Invalid token data");
            }

            String storedHash = parts[0];
            String email = parts[2];
            String role = parts[3];
            boolean wasUsed = Boolean.parseBoolean(parts[4]);

            // Verify token hash
            String providedHash = hashToken(refreshToken);
            if (!storedHash.equals(providedHash)) {
                log.warn("Refresh token hash mismatch for user: {}", payload.userId());
                throw new RefreshTokenException("Invalid refresh token");
            }

            // REUSE DETECTION: If token was already used, someone might have stolen it
            if (wasUsed) {
                log.warn("SECURITY: Refresh token reuse detected for user: {}. Revoking family: {}",
                        payload.userId(), payload.familyId());
                revokeFamily(payload.userId(), payload.familyId());
                throw new RefreshTokenException("Token reuse detected - session invalidated");
            }

            // Mark current token as used (for reuse detection)
            markTokenAsUsed(redisKey, storedValue);

            // Blacklist old access token if provided (security enhancement)
            if (oldAccessToken != null && !oldAccessToken.isBlank()) {
                try {
                    redisTokenBlacklistService.blacklistToken(oldAccessToken, payload.userId(), "REFRESH");
                    log.debug("Blacklisted old access token during refresh for user: {}", payload.userId());
                } catch (Exception e) {
                    log.warn("Failed to blacklist old access token during refresh: {}", e.getMessage());
                    // Don't fail the refresh if blacklisting fails
                }
            }

            // Generate new access token
            String newAccessToken = jwtUtil.generateToken(payload.userId(), email, role);

            // Rotate: generate new refresh token in the same family
            RefreshTokenData newRefreshData = generateTokenForFamily(
                    payload.userId(), email, role, payload.familyId());

            log.info("Refresh token rotated successfully for user: {}", payload.userId());

            return new TokenPair(newAccessToken, newRefreshData.token(),
                    jwtUtil.getAccessTokenTtlSeconds(), REFRESH_TOKEN_TTL_SECONDS);

        } catch (RefreshTokenException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error during refresh token validation", e);
            throw new RefreshTokenException("Token validation failed", e);
        }
    }

    /**
     * Revokes all refresh tokens for a user (used during logout).
     *
     * @param userId User's ID
     */
    public void revokeAllForUser(String userId) {
        try {
            String familySetKey = REFRESH_FAMILY_PREFIX + userId;
            Set<String> families = redisTemplate.opsForSet().members(familySetKey);

            if (families != null && !families.isEmpty()) {
                for (String familyId : families) {
                    String tokenKey = REFRESH_TOKEN_PREFIX + userId + ":" + familyId;
                    redisTemplate.delete(tokenKey);
                }
                redisTemplate.delete(familySetKey);
                log.info("Revoked {} refresh token families for user: {}", families.size(), userId);
            }
        } catch (Exception e) {
            log.error("Error revoking refresh tokens for user: {}", userId, e);
        }
    }

    /**
     * Revokes a specific token family (used for reuse detection).
     */
    public void revokeFamily(String userId, String familyId) {
        try {
            String tokenKey = REFRESH_TOKEN_PREFIX + userId + ":" + familyId;
            String familySetKey = REFRESH_FAMILY_PREFIX + userId;

            redisTemplate.delete(tokenKey);
            redisTemplate.opsForSet().remove(familySetKey, familyId);

            log.info("Revoked refresh token family: {} for user: {}", familyId, userId);
        } catch (Exception e) {
            log.error("Error revoking token family: {}", familyId, e);
        }
    }

    /**
     * Get the refresh token TTL in seconds.
     */
    public long getRefreshTokenTtlSeconds() {
        return REFRESH_TOKEN_TTL_SECONDS;
    }

    // --- Private Helper Methods ---

    private RefreshTokenData generateTokenForFamily(String userId, String email, String role, String familyId) {
        // Generate secure random token
        byte[] tokenBytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(tokenBytes);

        // Format: base64(random).userId.familyId
        String tokenValue = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
        String token = tokenValue + "." + userId + "." + familyId;

        // Hash for storage
        String tokenHash = hashToken(token);

        // Store in Redis: hash:createdAt:email:role:used
        String redisKey = REFRESH_TOKEN_PREFIX + userId + ":" + familyId;
        String redisValue = String.format("%s:%s:%s:%s:%s",
                tokenHash,
                Instant.now().toString(),
                email,
                role,
                "false");

        redisTemplate.opsForValue().set(redisKey, redisValue, Duration.ofSeconds(REFRESH_TOKEN_TTL_SECONDS));

        // Track family membership
        String familySetKey = REFRESH_FAMILY_PREFIX + userId;
        redisTemplate.opsForSet().add(familySetKey, familyId);
        redisTemplate.expire(familySetKey, Duration.ofSeconds(REFRESH_TOKEN_TTL_SECONDS + 60));

        log.debug("Generated refresh token for user: {}, family: {}", userId, familyId);

        return new RefreshTokenData(token, familyId, REFRESH_TOKEN_TTL_SECONDS);
    }

    private void markTokenAsUsed(String redisKey, String currentValue) {
        // Replace :false with :true at the end
        String newValue = currentValue.substring(0, currentValue.lastIndexOf(":")) + ":true";
        Long ttl = redisTemplate.getExpire(redisKey);
        if (ttl != null && ttl > 0) {
            redisTemplate.opsForValue().set(redisKey, newValue, Duration.ofSeconds(ttl));
        }
    }

    private RefreshTokenPayload parseRefreshToken(String token) {
        if (token == null || token.isBlank()) {
            throw new RefreshTokenException("Token is empty");
        }

        String[] parts = token.split("\\.", 3);
        if (parts.length != 3) {
            throw new RefreshTokenException("Invalid token format");
        }

        return new RefreshTokenPayload(parts[0], parts[1], parts[2]);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    // --- Data Classes ---

    public record RefreshTokenData(String token, String familyId, long ttlSeconds) {
    }

    public record TokenPair(String accessToken, String refreshToken, long accessTtl, long refreshTtl) {
    }

    private record RefreshTokenPayload(String tokenValue, String userId, String familyId) {
    }

    public static class RefreshTokenException extends RuntimeException {
        public RefreshTokenException(String message) {
            super(message);
        }

        public RefreshTokenException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
