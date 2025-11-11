package ru.reshaka.gateway.security.service;


import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;
import ru.reshaka.gateway.infra.redis.RefreshTokenStore;

import java.time.Instant;

@RequiredArgsConstructor
public class JwtService {
    private final JwtTokenGenerator generator;
    private final JwtTokenVerifier verifier;
    private final RefreshTokenStore store;

    public String generateAccess(UserDetails u) { return generator.generateAccess(u); }

    public Mono<String> generateAndStoreRefresh(UserDetails u) {
        String jti = java.util.UUID.randomUUID().toString();
        String token = generator.generateRefresh(u, jti);
        Instant exp = verifier.verifyRefresh(token).getExpiration().toInstant();
        return store.save(u.getUsername(), jti, exp).thenReturn(token);
    }

    public Claims validateAccess(String token) { return verifier.verifyAccess(token); }

    public Mono<Claims> validateRefreshActive(String token) {
        Claims c = verifier.verifyRefresh(token);
        return store.isActive(c.getSubject(), c.getId())
                .flatMap(active -> active ? Mono.just(c) : Mono.error(new IllegalStateException("refresh revoked")));
    }

    public Mono<Void> revokeRefresh(String username, String jti) { return store.revoke(username, jti); }
}

