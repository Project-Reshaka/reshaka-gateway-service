package ru.reshaka.gateway.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.reshaka.gateway.infra.postgre.model.UserPrincipal;
import ru.reshaka.gateway.infra.postgre.repo.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService implements ReactiveUserDetailsService {

    private final UserRepository userRepository;

    public Mono<UserPrincipal> save(UserPrincipal user) {
        return userRepository.save(user);
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
         return userRepository.findByUsername(username).cast(UserDetails.class);
    }
}
