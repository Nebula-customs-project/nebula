package pse.nebula.user.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class RedisTokenBlacklistServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private RedisTokenBlacklistService redisTokenBlacklistService;

    private String testToken;
    private String testJti;
    private String testUserId;
    private Date futureExpiration;
    private Claims mockClaims;

    @BeforeEach
    void setUp() {
        testToken = "test.jwt.token";
        testJti = "550e8400-e29b-41d4-a716-446655440000";
        testUserId = "user123";
        
        // Set expiration to 1 hour in the future
        futureExpiration = new Date(System.currentTimeMillis() + 3600000);
        
        // Create mock claims
        mockClaims = mock(Claims.class);
        
        // Setup RedisTemplate mock (using lenient to avoid unnecessary stubbing errors)
        lenient().when(mockClaims.getId()).thenReturn(testJti);
        lenient().when(mockClaims.getExpiration()).thenReturn(futureExpiration);
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void blacklistToken_WithValidToken_ShouldStoreInRedis() {
        // Arrange
        when(jwtUtil.parseToken(testToken)).thenReturn(mockClaims);

        // Act
        redisTokenBlacklistService.blacklistToken(testToken, testUserId, "LOGOUT");

        // Assert
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Duration> durationCaptor = ArgumentCaptor.forClass(Duration.class);
        
        verify(valueOperations).set(keyCaptor.capture(), valueCaptor.capture(), durationCaptor.capture());
        
        assertTrue(keyCaptor.getValue().contains(testJti));
        assertTrue(valueCaptor.getValue().contains(testUserId));
        assertTrue(valueCaptor.getValue().contains("LOGOUT"));
        assertTrue(durationCaptor.getValue().getSeconds() > 0);
    }

    @Test
    void blacklistToken_WithTokenMissingJti_ShouldThrowException() {
        // Arrange
        when(jwtUtil.parseToken(testToken)).thenReturn(mockClaims);
        when(mockClaims.getId()).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
            () -> redisTokenBlacklistService.blacklistToken(testToken, testUserId, "LOGOUT"));
        
        verify(valueOperations, never()).set(any(), any(), any(Duration.class));
    }

    @Test
    void blacklistToken_WithBlankJti_ShouldThrowException() {
        // Arrange
        when(jwtUtil.parseToken(testToken)).thenReturn(mockClaims);
        when(mockClaims.getId()).thenReturn("");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
            () -> redisTokenBlacklistService.blacklistToken(testToken, testUserId, "LOGOUT"));
        
        verify(valueOperations, never()).set(any(), any(), any(Duration.class));
    }

    @Test
    void blacklistToken_WithExpiredToken_ShouldNotStoreInRedis() {
        // Arrange
        Date pastExpiration = new Date(System.currentTimeMillis() - 1000); // 1 second ago
        when(mockClaims.getExpiration()).thenReturn(pastExpiration);
        when(jwtUtil.parseToken(testToken)).thenReturn(mockClaims);

        // Act
        redisTokenBlacklistService.blacklistToken(testToken, testUserId, "LOGOUT");

        // Assert
        verify(valueOperations, never()).set(any(), any(), any(Duration.class));
    }

    @Test
    void blacklistToken_WithTokenExpiringInLessThanOneSecond_ShouldNotStoreInRedis() {
        // Arrange
        Date almostExpired = new Date(System.currentTimeMillis() + 500); // 500ms in future
        when(mockClaims.getExpiration()).thenReturn(almostExpired);
        when(jwtUtil.parseToken(testToken)).thenReturn(mockClaims);

        // Act
        redisTokenBlacklistService.blacklistToken(testToken, testUserId, "LOGOUT");

        // Assert
        verify(valueOperations, never()).set(any(), any(), any(Duration.class));
    }

    @Test
    void blacklistToken_CalculatesTTLWithClockSkewBuffer() {
        // Arrange
        long ttlMillis = 3600000; // 1 hour
        Date expiration = new Date(System.currentTimeMillis() + ttlMillis);
        when(mockClaims.getExpiration()).thenReturn(expiration);
        when(jwtUtil.parseToken(testToken)).thenReturn(mockClaims);

        // Act
        redisTokenBlacklistService.blacklistToken(testToken, testUserId, "LOGOUT");

        // Assert
        ArgumentCaptor<Duration> durationCaptor = ArgumentCaptor.forClass(Duration.class);
        verify(valueOperations).set(any(), any(), durationCaptor.capture());
        
        // TTL should be approximately 3600 seconds + 60 second buffer
        long expectedMinTTL = 3660; // 1 hour + 60 seconds buffer
        assertTrue(durationCaptor.getValue().getSeconds() >= expectedMinTTL - 2); // Allow 2 second tolerance for test execution time
    }

    @Test
    void blacklistToken_WhenParseThrowsException_ShouldThrowIllegalStateException() {
        // Arrange
        when(jwtUtil.parseToken(testToken)).thenThrow(new RuntimeException("Invalid token"));

        // Act & Assert
        assertThrows(IllegalStateException.class, 
            () -> redisTokenBlacklistService.blacklistToken(testToken, testUserId, "LOGOUT"));
        
        verify(valueOperations, never()).set(any(), any(), any(Duration.class));
    }

    @Test
    void isBlacklisted_WithBlacklistedToken_ShouldReturnTrue() {
        // Arrange
        when(jwtUtil.getTokenId(testToken)).thenReturn(testJti);
        when(redisTemplate.hasKey("blacklist:jti:" + testJti)).thenReturn(true);

        // Act
        boolean result = redisTokenBlacklistService.isBlacklisted(testToken);

        // Assert
        assertTrue(result);
        verify(redisTemplate).hasKey("blacklist:jti:" + testJti);
    }

    @Test
    void isBlacklisted_WithNonBlacklistedToken_ShouldReturnFalse() {
        // Arrange
        when(jwtUtil.getTokenId(testToken)).thenReturn(testJti);
        when(redisTemplate.hasKey("blacklist:jti:" + testJti)).thenReturn(false);

        // Act
        boolean result = redisTokenBlacklistService.isBlacklisted(testToken);

        // Assert
        assertFalse(result);
        verify(redisTemplate).hasKey("blacklist:jti:" + testJti);
    }

    @Test
    void isBlacklisted_WithNullJti_ShouldReturnFalse() {
        // Arrange
        when(jwtUtil.getTokenId(testToken)).thenReturn(null);

        // Act
        boolean result = redisTokenBlacklistService.isBlacklisted(testToken);

        // Assert
        assertFalse(result);
        verify(redisTemplate, never()).hasKey(any());
    }

    @Test
    void isBlacklisted_WithBlankJti_ShouldReturnFalse() {
        // Arrange
        when(jwtUtil.getTokenId(testToken)).thenReturn("");

        // Act
        boolean result = redisTokenBlacklistService.isBlacklisted(testToken);

        // Assert
        assertFalse(result);
        verify(redisTemplate, never()).hasKey(any());
    }

    @Test
    void isBlacklisted_WhenRedisThrowsException_ShouldReturnFalse() {
        // Arrange
        when(jwtUtil.getTokenId(testToken)).thenReturn(testJti);
        when(redisTemplate.hasKey(any())).thenThrow(new RuntimeException("Redis connection error"));

        // Act
        boolean result = redisTokenBlacklistService.isBlacklisted(testToken);

        // Assert
        assertFalse(result); // Fail-safe behavior
    }

    @Test
    void getBlacklistDetails_WithBlacklistedToken_ShouldReturnDetails() {
        // Arrange
        String redisValue = "user123:LOGOUT:2024-01-01T12:00:00Z";
        when(jwtUtil.getTokenId(testToken)).thenReturn(testJti);
        when(valueOperations.get("blacklist:jti:" + testJti)).thenReturn(redisValue);

        // Act
        Optional<RedisTokenBlacklistService.BlacklistInfo> result = 
            redisTokenBlacklistService.getBlacklistDetails(testToken);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testJti, result.get().jti());
        assertEquals("user123", result.get().userId());
        assertEquals("LOGOUT", result.get().reason());
        assertEquals("2024-01-01T12:00:00Z", result.get().timestamp());
    }

    @Test
    void getBlacklistDetails_WithNonBlacklistedToken_ShouldReturnEmpty() {
        // Arrange
        when(jwtUtil.getTokenId(testToken)).thenReturn(testJti);
        when(valueOperations.get("blacklist:jti:" + testJti)).thenReturn(null);

        // Act
        Optional<RedisTokenBlacklistService.BlacklistInfo> result = 
            redisTokenBlacklistService.getBlacklistDetails(testToken);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void getBlacklistDetails_WithNullJti_ShouldReturnEmpty() {
        // Arrange
        when(jwtUtil.getTokenId(testToken)).thenReturn(null);

        // Act
        Optional<RedisTokenBlacklistService.BlacklistInfo> result = 
            redisTokenBlacklistService.getBlacklistDetails(testToken);

        // Assert
        assertFalse(result.isPresent());
        verify(valueOperations, never()).get(any());
    }

    @Test
    void getBlacklistDetails_WithMalformedValue_ShouldReturnEmpty() {
        // Arrange
        String malformedValue = "incomplete:data"; // Missing third part
        when(jwtUtil.getTokenId(testToken)).thenReturn(testJti);
        when(valueOperations.get("blacklist:jti:" + testJti)).thenReturn(malformedValue);

        // Act
        Optional<RedisTokenBlacklistService.BlacklistInfo> result = 
            redisTokenBlacklistService.getBlacklistDetails(testToken);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void getBlacklistDetails_WhenRedisThrowsException_ShouldReturnEmpty() {
        // Arrange
        when(jwtUtil.getTokenId(testToken)).thenReturn(testJti);
        when(valueOperations.get(any())).thenThrow(new RuntimeException("Redis error"));

        // Act
        Optional<RedisTokenBlacklistService.BlacklistInfo> result = 
            redisTokenBlacklistService.getBlacklistDetails(testToken);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void blacklistToken_StoresCorrectRedisKey() {
        // Arrange
        when(jwtUtil.parseToken(testToken)).thenReturn(mockClaims);

        // Act
        redisTokenBlacklistService.blacklistToken(testToken, testUserId, "LOGOUT");

        // Assert
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(valueOperations).set(keyCaptor.capture(), any(), any(Duration.class));
        
        assertEquals("blacklist:jti:" + testJti, keyCaptor.getValue());
    }

    @Test
    void blacklistToken_StoresCorrectValueFormat() {
        // Arrange
        when(jwtUtil.parseToken(testToken)).thenReturn(mockClaims);

        // Act
        redisTokenBlacklistService.blacklistToken(testToken, "user456", "SECURITY_BREACH");

        // Assert
        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);
        verify(valueOperations).set(any(), valueCaptor.capture(), any(Duration.class));
        
        String value = valueCaptor.getValue();
        String[] parts = value.split(":", 3);
        
        assertEquals(3, parts.length);
        assertEquals("user456", parts[0]);
        assertEquals("SECURITY_BREACH", parts[1]);
        assertNotNull(parts[2]); // Timestamp
    }
}
