package pse.nebula.gateway.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Client for checking token blacklist status from user-service
 * Supports both Spring Cloud LoadBalancer (Eureka) and direct URL configuration
 * (Docker)
 */
@Component
public class TokenBlacklistClient {

    private static final Logger log = LoggerFactory.getLogger(TokenBlacklistClient.class);

    private final WebClient webClient;
    private final String userServiceUrl;

    public TokenBlacklistClient(
            WebClient.Builder webClientBuilder,
            ReactorLoadBalancerExchangeFilterFunction lbFunction,
            @Value("${USER_SERVICE_URL:}") String directUrl) {

        // Use direct URL if provided (Docker), otherwise use load-balanced URL (Eureka)
        if (directUrl != null && !directUrl.isBlank()) {
            this.userServiceUrl = directUrl;
            this.webClient = webClientBuilder.build();
            log.info("TokenBlacklistClient configured with direct URL: {}", directUrl);
        } else {
            this.userServiceUrl = "http://user-service";
            this.webClient = webClientBuilder.filter(lbFunction).build();
            log.info("TokenBlacklistClient configured with load-balanced URL: {}", this.userServiceUrl);
        }
    }

    /**
     * Check if a token is blacklisted by calling user-service
     *
     * @param token JWT token to check
     * @return Mono<Boolean> true if blacklisted, false otherwise
     */
    public Mono<Boolean> isTokenBlacklisted(String token) {
        log.debug("Checking if token is blacklisted via {}", userServiceUrl);

        return webClient.get()
                .uri(userServiceUrl + "/api/users/blacklist/check")
                .header("X-Token-Check", token)
                .retrieve()
                .bodyToMono(Boolean.class)
                .timeout(Duration.ofSeconds(2))
                .doOnSuccess(isBlacklisted -> {
                    if (Boolean.TRUE.equals(isBlacklisted)) {
                        log.warn("Token is blacklisted");
                    } else {
                        log.debug("Token is not blacklisted");
                    }
                })
                .doOnError(error -> log.error("Failed to check token blacklist at {}: {}", userServiceUrl,
                        error.getMessage()))
                .onErrorReturn(false); // On error, assume not blacklisted to avoid blocking legitimate requests
    }
}
