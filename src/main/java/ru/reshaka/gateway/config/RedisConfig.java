package ru.reshaka.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import ru.reshaka.gateway.infra.redis.RedisRefreshTokenStore;
import ru.reshaka.gateway.infra.redis.RefreshTokenStore;

@Configuration
public class RedisConfig {

    public @Bean RefreshTokenStore refreshTokenStore(@Autowired ReactiveStringRedisTemplate reactiveStringRedisTemplate) {
        return new RedisRefreshTokenStore(reactiveStringRedisTemplate);
    }

    @Bean
    public ReactiveStringRedisTemplate reactiveStringRedisTemplate(@Autowired ReactiveRedisConnectionFactory factory) {
        return new ReactiveStringRedisTemplate(factory);
    }

}
