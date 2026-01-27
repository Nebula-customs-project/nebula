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
import pse.nebula.gateway.config.PublicRoutesConfig;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtValidator jwtValidator;

    @Autowired
    private PublicRoutesConfig publicRoutesConfig;

    @Autowired
    private TokenBlacklistClient tokenBlacklistClient;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        log.debug("Processing request: {} {}", request.getMethod(), path);

        // Skip WebSocket paths - handled by WebSocketAuthenticationFilter
        if (path.startsWith("/ws/")) {
            log.debug("WebSocket path, delegating to WebSocketAuthenticationFilter: {}", path);
            return chain.filter(exchange);
        }

        // Skip authentication for public routes
        if (publicRoutesConfig.isPublicRoute(path)) {
            log.debug("Public route, skipping authentication: {}", path);
            return chain.filter(exchange);
        }

        // Extract token from Authorization header or Cookie
        String extractedToken = null;
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            extractedToken = authHeader.substring(7);
        } else {
            // Try to get from cookie
            var cookie = request.getCookies().getFirst("access_token");
            if (cookie != null) {
                extractedToken = cookie.getValue();
            }
        }

        if (extractedToken == null) {
            log.warn("Missing or invalid Authorization header/cookie for: {}", path);
            return onError(exchange, "Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED);
        }

        final String token = extractedToken;

        log.debug("Extracted token for path: {}", path);

        // Check if token is blacklisted
        return tokenBlacklistClient.isTokenBlacklisted(token)
                .flatMap(isBlacklisted -> {
                    log.debug("Blacklist check result for path {}: {}", path, isBlacklisted);

                    if (Boolean.TRUE.equals(isBlacklisted)) {
                        log.warn("Blocked blacklisted token for path: {}", path);
                        return onError(exchange, "Token has been revoked", HttpStatus.UNAUTHORIZED);
                    }

                    log.debug("Token not blacklisted, proceeding with JWT validation");

                    try {
                        // Validate token
                        Claims claims = jwtValidator.validateToken(token);

                        // Extract user information
                        String userId = jwtValidator.getUserId(claims);
                        String email = jwtValidator.getEmail(claims);
                        String roles = jwtValidator.getRoles(claims);

                        // Add user info and Authorization header (if missing) for downstream services
                        ServerHttpRequest.Builder builder = request.mutate()
                                .header("X-User-Id", userId != null ? userId : "")
                                .header("X-User-Email", email != null ? email : "")
                                .header("X-User-Roles", roles != null ? roles : "");

                        // Ensure Authorization header is present for downstream services (like logout)
                        if (authHeader == null && token != null) {
                            builder.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
                        }

                        ServerHttpRequest modifiedRequest = builder.build();

                        // Continue with modified request
                        return chain.filter(exchange.mutate().request(modifiedRequest).build());

                    } catch (Exception e) {
                        log.error("Token validation failed: {}", e.getMessage());
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
        return -100; // Run before other filters
    }

    // Package-visible setter used by tests to inject mock TokenBlacklistClient
    // reliably
    void setTokenBlacklistClient(TokenBlacklistClient tokenBlacklistClient) {
        this.tokenBlacklistClient = tokenBlacklistClient;
    }
}
