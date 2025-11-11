package ru.reshaka.gateway.infra.redis;

import reactor.core.publisher.Mono;

import java.time.Instant;

public interface RefreshTokenStore {
    Mono<Void> save(String username, String jti, Instant expiresAt); // при выдаче
    Mono<Boolean> isActive(String username, String jti);             // при проверке
    Mono<Void> revoke(String username, String jti);                  // при логауте/ротации
}
