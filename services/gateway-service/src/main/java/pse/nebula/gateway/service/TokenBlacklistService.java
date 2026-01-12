package pse.nebula.gateway.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pse.nebula.gateway.repository.BlacklistedTokenRepository;
import reactor.core.publisher.Mono;

@Service
public class TokenBlacklistService {

    private static final Logger log = LoggerFactory.getLogger(TokenBlacklistService.class);

    @Autowired
    private BlacklistedTokenRepository blacklistedTokenRepository;

    /**
     * Check if token is blacklisted (reactive)
     */
    public Mono<Boolean> isBlacklisted(String token) {
        return blacklistedTokenRepository.existsByToken(token)
                .doOnNext(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        log.warn("Blocked blacklisted token");
                    }
                })
                .onErrorResume(error -> {
                    // Fail-safe: if database is down, don't block requests
                    log.error("Database error checking blacklist: {}", error.getMessage());
                    return Mono.just(false);
                });
    }
}
