package ru.reshaka.gateway.security.filter;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import ru.reshaka.gateway.infra.postgre.repo.UserRepository;
import ru.reshaka.gateway.security.service.JwtService;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAndUserIdFilter implements WebFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.info("[JWT+USERID FILTER] Start filter for request: {} on thread {}", exchange.getRequest().getURI(), Thread.currentThread().getName());

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        log.info("[JWT+USERID FILTER] Authorization header: {}", authHeader);

        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            log.info("[JWT+USERID FILTER] No Bearer token found, continue without auth");
            return chain.filter(exchange);
        }

        String token = authHeader.substring("Bearer ".length());
        log.info("[JWT+USERID FILTER] Token extracted");

        Claims claims;
        try {
            claims = jwtService.validateAccess(token);
            log.info("[JWT+USERID FILTER] Token validated, claims: {}", claims);
        } catch (Exception e) {
            log.error("[JWT+USERID FILTER] Token validation failed", e);
            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid JWT token"));
        }

        String username = claims.getSubject();
        log.info("[JWT+USERID FILTER] Username from token: {}", username);

        List<SimpleGrantedAuthority> authorities = claims.get("authorities", List.class)
                .stream()
                .map(m -> new SimpleGrantedAuthority(((Map)m).get("authority").toString()))
                .toList();

        Authentication auth = new UsernamePasswordAuthenticationToken(username, null, authorities);


        return userRepository.findByUsername(username)
                .map(user -> {
                    log.info("[JWT+USERID FILTER] User found in DB: {}", user.getId());

                    return exchange.mutate()
                            .request(r -> r.headers(h -> h.add("X-User-Id", user.getId().toString())))
                            .build();

                })
                .flatMap(ex -> chain.filter(ex).contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)))
                .doOnError(e -> log.error(e.getMessage(), e));


    }
}


