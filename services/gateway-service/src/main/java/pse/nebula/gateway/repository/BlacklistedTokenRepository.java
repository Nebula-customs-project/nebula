package pse.nebula.gateway.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface BlacklistedTokenRepository extends ReactiveCrudRepository<BlacklistedTokenEntity, Long> {
    
    @Query("SELECT COUNT(*) > 0 FROM blacklisted_tokens WHERE token = :token")
    Mono<Boolean> existsByToken(String token);
}
