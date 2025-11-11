package ru.reshaka.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.reshaka.gateway.infra.redis.RefreshTokenStore;
import ru.reshaka.gateway.security.service.JwtService;
import ru.reshaka.gateway.security.service.JwtTokenGenerator;
import ru.reshaka.gateway.security.service.JwtTokenVerifier;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Clock;

@Configuration
public class JwtConfig {

    public @Bean JwtTokenVerifier jwtTokenVerifier(@Autowired PublicKey accessPublicKey, @Autowired PublicKey refreshPublicKey) {
        return new JwtTokenVerifier(accessPublicKey, refreshPublicKey);
    }

    public @Bean JwtTokenGenerator tokenGenerator(@Autowired PrivateKey accessPrivateKey, @Autowired PrivateKey refreshPrivateKey) {
        return new JwtTokenGenerator(accessPrivateKey, refreshPrivateKey, Clock.systemUTC());
    }

    public @Bean JwtService jwtService(@Autowired JwtTokenVerifier jwtTokenVerifier,
                                       @Autowired JwtTokenGenerator jwtTokenGenerator,
                                       @Autowired RefreshTokenStore refreshTokenStore) {
        return new JwtService(jwtTokenGenerator, jwtTokenVerifier, refreshTokenStore);
    }

}
