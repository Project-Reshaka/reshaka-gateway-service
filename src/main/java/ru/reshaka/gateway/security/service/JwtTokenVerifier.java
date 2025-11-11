package ru.reshaka.gateway.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

import java.security.PublicKey;

@RequiredArgsConstructor
public class JwtTokenVerifier {
    private final PublicKey accessPublicKey;
    private final PublicKey refreshPublicKey;

    public Claims verifyAccess(String token) {
        Claims c = Jwts.parser().verifyWith(accessPublicKey).build()
                .parseSignedClaims(token).getPayload();
        if (!"access".equals(c.get("typ"))) throw new IllegalArgumentException("wrong typ");
        return c;
    }

    public Claims verifyRefresh(String token) {
        Claims c = Jwts.parser().verifyWith(refreshPublicKey).build()
                .parseSignedClaims(token).getPayload();
        if (!"refresh".equals(c.get("typ"))) throw new IllegalArgumentException("wrong typ");
        return c;
    }
}

