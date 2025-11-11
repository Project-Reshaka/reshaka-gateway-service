package ru.reshaka.gateway.security.service;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.PrivateKey;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;

@RequiredArgsConstructor
public class JwtTokenGenerator {
    private final PrivateKey accessPrivateKey;
    private final PrivateKey refreshPrivateKey;
    private final Clock clock;

    private static final long ACCESS_TTL_MS = 1500 * 60 * 1000; // ЧИСТО ДЛЯ УДОБСТВА ДЕБАГА СДЕЛАЛ ПОБОЛЬШЕ
    private static final long REFRESH_TTL_MS = 7L * 24 * 60 * 60 * 1000;

    public String generateAccess(UserDetails user) {
        Instant now = clock.instant();
        return Jwts.builder()
                .claim("authorities", user.getAuthorities())
                .subject(user.getUsername())
                .issuedAt(Date.from(now))
                .expiration(new Date(now.toEpochMilli() + ACCESS_TTL_MS))
                .claim("typ", "access")
                .signWith(accessPrivateKey)
                .compact();
    }

    public String generateRefresh(UserDetails user, String jti) {
        Instant now = clock.instant();
        return Jwts.builder()
                .subject(user.getUsername())
                .id(jti)                 // jti для хранения/инвалидации
                .issuedAt(Date.from(now))
                .expiration(new Date(now.toEpochMilli() + REFRESH_TTL_MS))
                .claim("typ", "refresh")
                .signWith(refreshPrivateKey)
                .compact();
    }
}
