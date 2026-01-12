package pse.nebula.gateway.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class TokenBlacklistService {

    private static final Logger log = LoggerFactory.getLogger(TokenBlacklistService.class);
    private static final String BLACKLIST_PREFIX = "blacklist:";

    @Autowired
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    /**
     * Check if token is blacklisted (reactive)
     */
    public Mono<Boolean> isBlacklisted(String token) {
        String key = BLACKLIST_PREFIX + token;
        
        return reactiveRedisTemplate.hasKey(key)
                .doOnNext(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        log.warn("Blocked blacklisted token");
                    }
                })
                .onErrorResume(error -> {
                    // Fail-safe: if Redis is down, don't block requests
                    log.error("Redis error checking blacklist: {}", error.getMessage());
                    return Mono.just(false);
                });
    }
}
