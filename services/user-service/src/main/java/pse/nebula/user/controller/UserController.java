package pse.nebula.user.controller;

import pse.nebula.user.dto.AuthenticationResult;
import pse.nebula.user.dto.RefreshTokenRequest;
import pse.nebula.user.dto.TokenResponse;
import pse.nebula.user.model.User;
import pse.nebula.user.service.UserService;
import pse.nebula.user.service.RedisTokenBlacklistService;
import pse.nebula.user.service.RefreshTokenService;
import pse.nebula.user.service.RefreshTokenService.RefreshTokenException;
import pse.nebula.user.dto.LoginRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {

    // Cookie names
    private static final String ACCESS_TOKEN_COOKIE = "access_token";
    private static final String REFRESH_TOKEN_COOKIE = "refresh_token";

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTokenBlacklistService redisTokenBlacklistService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    /**
     * Register a new user
     * POST /users/register
     */
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@Valid @RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Login user and get JWT tokens (access + refresh)
     * Returns tokens in both response body AND HttpOnly cookies.
     * POST /users/login
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            AuthenticationResult authResult = userService.authenticateUser(
                    loginRequest.getEmail(), loginRequest.getPassword());
            User user = authResult.getUser();

            TokenResponse response = TokenResponse.forLogin(
                    authResult.getToken(),
                    authResult.getRefreshToken(),
                    authResult.getAccessTokenTtl(),
                    authResult.getRefreshTokenTtl(),
                    new TokenResponse.UserInfo(
                            user.getId(),
                            user.getEmail(),
                            user.getUsername(),
                            user.getRole().name()));

            // Set HttpOnly cookies for secure token storage
            ResponseCookie accessCookie = createAccessTokenCookie(
                    authResult.getToken(), authResult.getAccessTokenTtl());
            ResponseCookie refreshCookie = createRefreshTokenCookie(
                    authResult.getRefreshToken(), authResult.getRefreshTokenTtl());

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                    .body(response);

        } catch (RuntimeException e) {
            log.warn("Login failed for email: {}", loginRequest.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Refresh access token using refresh token.
     * Returns new tokens in both response body AND HttpOnly cookies.
     * If Authorization header with old access token is provided, it will be
     * blacklisted.
     * POST /users/auth/refresh
     */
    @PostMapping("/auth/refresh")
    public ResponseEntity<?> refreshToken(
            @RequestBody(required = false) RefreshTokenRequest request,
            @CookieValue(name = REFRESH_TOKEN_COOKIE, required = false) String refreshTokenCookie,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        try {
            // Extract old access token if provided (for blacklisting)
            String oldAccessToken = null;
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                oldAccessToken = authHeader.substring(7);
            }

            // Determine which refresh token to use
            String refreshTokenToUse = null;
            if (request != null && request.refreshToken() != null && !request.refreshToken().isEmpty()) {
                refreshTokenToUse = request.refreshToken();
            } else if (refreshTokenCookie != null && !refreshTokenCookie.isEmpty()) {
                refreshTokenToUse = refreshTokenCookie;
            }

            if (refreshTokenToUse == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Refresh token is missing (neither in body nor cookie)"));
            }

            RefreshTokenService.TokenPair tokenPair = refreshTokenService.validateAndRotate(
                    refreshTokenToUse, oldAccessToken);

            TokenResponse response = TokenResponse.forRefresh(
                    tokenPair.accessToken(),
                    tokenPair.refreshToken(),
                    tokenPair.accessTtl(),
                    tokenPair.refreshTtl());

            // Set new HttpOnly cookies
            ResponseCookie accessCookie = createAccessTokenCookie(
                    tokenPair.accessToken(), tokenPair.accessTtl());
            ResponseCookie refreshCookie = createRefreshTokenCookie(
                    tokenPair.refreshToken(), tokenPair.refreshTtl());

            log.info("Token refreshed successfully");

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                    .body(response);

        } catch (RefreshTokenException e) {
            log.warn("Refresh token validation failed: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    /**
     * Logout user: blacklist access token + revoke refresh token family.
     * Clears HttpOnly cookies.
     * POST /users/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logoutUser(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        try {
            // Extract token from "Bearer <token>" header
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Missing or invalid Authorization header");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            String token = authHeader.substring(7); // Remove "Bearer " prefix

            // Get user ID from authentication context
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication != null ? authentication.getName() : "UNKNOWN";

            // Blacklist access token in Redis
            redisTokenBlacklistService.blacklistToken(token, userId, "LOGOUT");

            // Revoke all refresh tokens for this user
            userService.revokeUserRefreshTokens(userId);

            // Clear cookies (Path must match the creation path "/")
            ResponseCookie clearAccess = clearCookie(ACCESS_TOKEN_COOKIE, "/");
            ResponseCookie clearRefresh = clearCookie(REFRESH_TOKEN_COOKIE, "/");

            Map<String, String> response = new HashMap<>();
            response.put("message", "Logged out successfully");

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, clearAccess.toString())
                    .header(HttpHeaders.SET_COOKIE, clearRefresh.toString())
                    .body(response);

        } catch (Exception e) {
            log.error("Logout failed", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Logout failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Check if a token is blacklisted
     * GET /users/blacklist/check
     */
    @GetMapping("/blacklist/check")
    public ResponseEntity<Boolean> checkTokenBlacklist(@RequestHeader("X-Token-Check") String token) {
        try {
            boolean isBlacklisted = redisTokenBlacklistService.isBlacklisted(token);
            return ResponseEntity.ok(isBlacklisted);
        } catch (Exception e) {
            log.error("Error checking token blacklist", e);
            return ResponseEntity.ok(false);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update user profile
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof String)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String authenticatedUserId = (String) principal;
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!authenticatedUserId.equals(id.toString()) && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (!userService.getUserById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        user.setId(id);
        return ResponseEntity.ok(userService.updateUser(user));
    }

    // --- Cookie Helper Methods ---

    private ResponseCookie createAccessTokenCookie(String token, long ttlSeconds) {
        return ResponseCookie.from(ACCESS_TOKEN_COOKIE, token)
                .httpOnly(true)
                .secure(false) // Set to false for local development (HTTP), true for Production (HTTPS)
                .path("/")
                .maxAge(ttlSeconds)
                .sameSite("Lax")
                .build();
    }

    private ResponseCookie createRefreshTokenCookie(String token, long ttlSeconds) {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE, token)
                .httpOnly(true)
                .secure(false) // Set to false for local development (HTTP), true for Production (HTTPS)
                .path("/")
                .maxAge(ttlSeconds)
                .sameSite("Lax")
                .build();
    }

    private ResponseCookie clearCookie(String name, String path) {
        return ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(false)
                .path(path)
                .maxAge(0)
                .build();
    }
}