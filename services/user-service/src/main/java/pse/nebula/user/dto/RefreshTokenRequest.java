package pse.nebula.user.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request body for token refresh endpoint.
 */
public record RefreshTokenRequest(
        @NotBlank(message = "Refresh token is required") String refreshToken) {
}
