package pse.nebula.user.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pse.nebula.user.model.BlacklistedToken;
import pse.nebula.user.repository.BlacklistedTokenRepository;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;

@Service
@Slf4j
public class TokenBlacklistService {

    @Autowired
    private BlacklistedTokenRepository blacklistedTokenRepository;

    @Value("${jwt.test-public-key}")
    private String publicKeyPem;

    @Transactional
    public void blacklistToken(String token) {
        try {
            // Parse the token to get expiration time
            Claims claims = parseToken(token);
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
            throw new RuntimeException("Failed to blacklist token", e);
        }
    }

    public boolean isBlacklisted(String token) {
        try {
            return blacklistedTokenRepository.existsByToken(token);
        } catch (Exception e) {
            log.error("Error checking blacklist", e);
            return false; // Fail-safe: if database is down, don't block the request
        }
    }

    // Cleanup expired tokens every hour
    @Scheduled(fixedRate = 3600000) // 1 hour in milliseconds
    @Transactional
    public void cleanupExpiredTokens() {
        try {
            blacklistedTokenRepository.deleteExpiredTokens(LocalDateTime.now());
            log.info("Cleaned up expired blacklisted tokens");
        } catch (Exception e) {
            log.error("Error cleaning up expired tokens", e);
        }
    }

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

    private PublicKey getPublicKey() {
        try {
            if (publicKeyPem == null || publicKeyPem.isBlank()) {
                throw new IllegalStateException("JWT public key is not configured");
            }

            String publicKeyContent = publicKeyPem
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] keyBytes = Base64.getDecoder().decode(publicKeyContent);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(spec);
        } catch (Exception e) {
            log.error("Failed to load public key: {}", e.getMessage());
            throw new RuntimeException("Failed to load JWT public key", e);
        }
    }
}
