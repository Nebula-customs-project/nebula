package pse.nebula.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Unified response for login and refresh endpoints.
 * Contains both access and refresh tokens with their TTLs.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TokenResponse(
        String accessToken,
        String refreshToken,
        long expiresIn, // Access token TTL in seconds
        long refreshExpiresIn, // Refresh token TTL in seconds
        UserInfo user // Only included in login response
) {
    /**
     * Nested user info for login response.
     */
    public record UserInfo(
            Long id,
            String email,
            String username,
            String role) {
    }

    /**
     * Factory for login response (includes user info).
     */
    public static TokenResponse forLogin(String accessToken, String refreshToken,
            long expiresIn, long refreshExpiresIn, UserInfo user) {
        return new TokenResponse(accessToken, refreshToken, expiresIn, refreshExpiresIn, user);
    }

    /**
     * Factory for refresh response (no user info).
     */
    public static TokenResponse forRefresh(String accessToken, String refreshToken,
            long expiresIn, long refreshExpiresIn) {
        return new TokenResponse(accessToken, refreshToken, expiresIn, refreshExpiresIn, null);
    }
}
