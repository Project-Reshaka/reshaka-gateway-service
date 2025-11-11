package ru.reshaka.gateway.security.service;


import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.reshaka.gateway.dto.AuthorizationRequestDto;
import ru.reshaka.gateway.dto.SignUpRequestDto;
import ru.reshaka.gateway.infra.postgre.model.UserPrincipal;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public Mono<AuthBundle> authorize(AuthorizationRequestDto dto) {
        return userService.findByUsername(dto.getUsername())
                .filter(u -> encoder.matches(dto.getPassword(), u.getPassword()))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("bad creds")))
                .flatMap(u -> jwtService.generateAndStoreRefresh(u)
                        .zipWith(Mono.fromCallable(() -> jwtService.generateAccess(u)),
                                (refresh, access) -> new AuthBundle(access, refresh)));
    }

    public Mono<AuthBundle> signup(SignUpRequestDto dto) {
        var user = new UserPrincipal();
        user.setUsername(dto.getUsername());
        user.setPasswordHash(encoder.encode(dto.getPassword()));
        return userService.save(user)
                .then(authorize(new AuthorizationRequestDto(dto.getUsername(), dto.getPassword())));
    }

    public Mono<AuthBundle> refresh(String refreshToken) {
        return jwtService.validateRefreshActive(refreshToken)
                .flatMap(c -> userService.findByUsername(c.getSubject()).zipWith(Mono.just(c)))
                .flatMap((uc) -> {
                    // ротация: инвалидируем старый и выдаем новый
                    return jwtService.revokeRefresh(uc.getT1().getUsername(), uc.getT2().getId())
                            .then(jwtService.generateAndStoreRefresh(uc.getT1())
                                    .zipWith(Mono.fromCallable(() -> jwtService.generateAccess(uc.getT1())),
                                            (newRefresh, access) -> new AuthBundle(access, newRefresh)));
                });
    }

    public record AuthBundle(String access, String refresh) {}
}

