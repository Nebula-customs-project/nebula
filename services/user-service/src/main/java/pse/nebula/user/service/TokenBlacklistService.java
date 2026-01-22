package pse.nebula.user.service;

import io.jsonwebtoken.Claims;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import pse.nebula.user.model.BlacklistedToken;
import pse.nebula.user.repository.BlacklistedTokenRepository;

/**
 * @deprecated This database-based token blacklist service has been replaced by {@link RedisTokenBlacklistService}.
 * The Redis-based implementation provides better performance with automatic TTL expiration and eliminates
 * the need for scheduled cleanup tasks. This class is kept for backward compatibility but is no longer
 * registered as a Spring bean to avoid bean definition conflicts.
 * 
 * @see RedisTokenBlacklistService
 */
@Deprecated(since = "1.0", forRemoval = true)
@Slf4j
public class TokenBlacklistService {

    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final JwtUtil jwtUtil;

    public TokenBlacklistService(BlacklistedTokenRepository blacklistedTokenRepository, JwtUtil jwtUtil) {
        this.blacklistedTokenRepository = blacklistedTokenRepository;
        this.jwtUtil = jwtUtil;
    }

    public void blacklistToken(String token) {
        try {
            // Parse the token to get expiration time using JwtUtil
            Claims claims = jwtUtil.parseToken(token);
            Date expiration = claims.getExpiration();

            LocalDateTime expiresAt = expiration.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            // Check if token is already expired
            if (expiresAt.isAfter(LocalDateTime.now())) {
                // Save to database
                BlacklistedToken blacklistedToken = new BlacklistedToken(token, expiresAt);
                blacklistedTokenRepository.save(blacklistedToken);
                log.info("Token blacklisted successfully for user: {}, expires at: {}", 
                         claims.getSubject(), expiresAt);
            } else {
                log.warn("Token already expired, not adding to blacklist");
            }
        } catch (Exception e) {
            log.error("Error blacklisting token", e);
            throw new IllegalStateException("Failed to blacklist token", e);
        }
    }

    // Currently unused
    public boolean isBlacklisted(String token) {
        try {
            return blacklistedTokenRepository.existsByToken(token);
        } catch (Exception e) {
            log.error("Error checking blacklist", e);
            throw new IllegalStateException("Failed to check token blacklist", e);
        }
    }

    // Cleanup expired tokens every hour
    // Note: This method will not execute since this class is no longer a Spring bean
    public void cleanupExpiredTokens() {
        try {
            blacklistedTokenRepository.deleteExpiredTokens(LocalDateTime.now());
            log.info("Cleaned up expired blacklisted tokens");
        } catch (Exception e) {
            log.error("Error cleaning up expired tokens", e);
        }
    }
}
