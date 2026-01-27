package pse.nebula.gateway.security;

import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * WebSocket Authentication Filter
 * 
 * Handles JWT authentication for WebSocket upgrade requests.
 * Extracts JWT token from query parameter (?token=xxx) since browsers
 * cannot send custom headers on WebSocket connections.
 * 
 * After validation, injects X-User-Id header for downstream services.
 */
@Component
public class WebSocketAuthenticationFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(WebSocketAuthenticationFilter.class);
    private static final String TOKEN_QUERY_PARAM = "token";
    private static final String WEBSOCKET_PATH_PREFIX = "/ws/";

    @Autowired
    private JwtValidator jwtValidator;

    @Autowired
    private TokenBlacklistClient tokenBlacklistClient;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // Only process WebSocket paths
        if (!path.startsWith(WEBSOCKET_PATH_PREFIX)) {
            return chain.filter(exchange);
        }

        // Check if this is a WebSocket upgrade request
        String upgradeHeader = request.getHeaders().getFirst(HttpHeaders.UPGRADE);
        if (upgradeHeader == null || !upgradeHeader.equalsIgnoreCase("websocket")) {
            // Not a WebSocket upgrade, let it pass (might be a regular HTTP request to
            // /ws/)
            return chain.filter(exchange);
        }

        log.debug("Processing WebSocket upgrade request: {}", path);

        // Extract token from query parameter
        String token = request.getQueryParams().getFirst(TOKEN_QUERY_PARAM);

        // Fallback: Check for access_token cookie if query param is missing
        if (token == null || token.isBlank()) {
            var cookie = request.getCookies().getFirst("access_token");
            if (cookie != null) {
                token = cookie.getValue();
                log.debug("Extracted token from cookie for WebSocket: {}", path);
            }
        }

        if (token == null || token.isBlank()) {
            log.warn("WebSocket connection rejected: missing token query parameter or cookie for: {}", path);
            return onError(exchange, "Missing authentication token", HttpStatus.UNAUTHORIZED);
        }

        final String finalToken = token;

        // Check if token is blacklisted
        return tokenBlacklistClient.isTokenBlacklisted(finalToken)
                .flatMap(isBlacklisted -> {
                    if (Boolean.TRUE.equals(isBlacklisted)) {
                        log.warn("WebSocket connection rejected: blacklisted token for: {}", path);
                        return onError(exchange, "Token has been revoked", HttpStatus.UNAUTHORIZED);
                    }

                    try {
                        // Validate token
                        Claims claims = jwtValidator.validateToken(finalToken);

                        // Extract user information
                        String userId = jwtValidator.getUserId(claims);
                        String email = jwtValidator.getEmail(claims);
                        String roles = jwtValidator.getRoles(claims);

                        log.info("WebSocket authenticated for user: {} on path: {}", userId, path);

                        // Add user info to request headers for downstream services
                        ServerHttpRequest modifiedRequest = request.mutate()
                                .header("X-User-Id", userId != null ? userId : "")
                                .header("X-User-Email", email != null ? email : "")
                                .header("X-User-Roles", roles != null ? roles : "")
                                .build();

                        // Continue with modified request
                        return chain.filter(exchange.mutate().request(modifiedRequest).build());

                    } catch (Exception e) {
                        log.error("WebSocket token validation failed: {}", e.getMessage());
                        return onError(exchange, "Invalid or expired token", HttpStatus.UNAUTHORIZED);
                    }
                });
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");

        String errorResponse = String.format("{\"error\":\"%s\",\"message\":\"%s\"}",
                status.getReasonPhrase(), message);

        return response.writeWith(Mono.just(response.bufferFactory()
                .wrap(errorResponse.getBytes())));
    }

    @Override
    public int getOrder() {
        // Run before JwtAuthenticationFilter (-100) to handle WebSocket paths first
        return -200;
    }

    // Package-visible setter for testing
    void setJwtValidator(JwtValidator jwtValidator) {
        this.jwtValidator = jwtValidator;
    }

    void setTokenBlacklistClient(TokenBlacklistClient tokenBlacklistClient) {
        this.tokenBlacklistClient = tokenBlacklistClient;
    }
}
