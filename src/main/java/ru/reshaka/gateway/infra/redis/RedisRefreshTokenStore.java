package ru.reshaka.gateway.infra.redis;

import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

public class RedisRefreshTokenStore implements RefreshTokenStore {

    private final ReactiveStringRedisTemplate redis;

    public RedisRefreshTokenStore(ReactiveStringRedisTemplate redis) {
        this.redis = redis;
    }

    private String key(String username, String jti) {
        return "refresh:" + username + ":" + jti;
    }

    @Override
    public Mono<Void> save(String username, String jti, Instant expiresAt) {
        Duration ttl = Duration.between(Instant.now(), expiresAt);
        if (ttl.isNegative() || ttl.isZero()) {
            return Mono.empty();
        }
        return redis.opsForValue()
                .set(key(username, jti), "1", ttl)
                .then();
    }

    @Override
    public Mono<Boolean> isActive(String username, String jti) {
        return redis.opsForValue()
                .get(key(username, jti))
                .map(val -> true)
                .defaultIfEmpty(false);
    }

    @Override
    public Mono<Void> revoke(String username, String jti) {
        return redis.opsForValue()
                .delete(key(username, jti))
                .then();
    }
}
