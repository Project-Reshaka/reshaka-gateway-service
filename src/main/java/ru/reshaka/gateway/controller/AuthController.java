package ru.reshaka.gateway.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.reshaka.gateway.dto.AuthenticationResponse;
import ru.reshaka.gateway.dto.AuthorizationRequestDto;
import ru.reshaka.gateway.dto.SignUpRequestDto;
import ru.reshaka.gateway.security.service.AuthService;
import ru.reshaka.gateway.security.service.JwtService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    private final JwtService jwtService;


    @PostMapping("login")
    public Mono<ResponseEntity<AuthenticationResponse>> login(@RequestBody AuthorizationRequestDto dto) {
        return authService.authorize(dto).map(this::withTokens);
    }

    @PostMapping("signup")
    public Mono<ResponseEntity<AuthenticationResponse>> signup(@RequestBody SignUpRequestDto dto) {
        return authService.signup(dto).map(this::withTokens);
    }

    @PostMapping("refresh")
    public Mono<ResponseEntity<AuthenticationResponse>> refresh(@CookieValue("refresh_token") String refreshCookie) {
        return authService.refresh(refreshCookie).map(this::withTokens);
    }

    @PostMapping("logout")
    public Mono<ResponseEntity<Void>> logout(@CookieValue("refresh_token") String refreshCookie) {
        // извлекаем jti, выключаем и чистим cookie
        return Mono.fromCallable(() -> jwtService.validateRefreshActive(refreshCookie))
                .flatMap(m -> m)
                .flatMap(c -> jwtService.revokeRefresh(c.getSubject(), c.getId()))
                .then(Mono.fromCallable(() ->
                        ResponseEntity.noContent()
                                .header("Set-Cookie", deleteRefreshCookie())
                                .build()));
    }

    private ResponseEntity<AuthenticationResponse> withTokens(AuthService.AuthBundle b) {
        AuthenticationResponse body = new AuthenticationResponse(b.access(), 900);
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + b.access())
                .header("Set-Cookie", buildRefreshCookie(b.refresh()))
                .body(body);
    }

    private String buildRefreshCookie(String token) {
        return "refresh_token=" + token + "; HttpOnly; Secure; SameSite=Strict; Path=/api/auth; Max-Age=" + 7*24*60*60;
    }

    private String deleteRefreshCookie() {
        return "refresh_token=; HttpOnly; Secure; SameSite=Strict; Path=/api/auth; Max-Age=0";
    }
}

