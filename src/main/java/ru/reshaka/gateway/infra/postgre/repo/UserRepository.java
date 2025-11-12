package ru.reshaka.gateway.infra.postgre.repo;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.reshaka.gateway.infra.postgre.model.UserPrincipal;

import java.util.UUID;

@Repository
public interface UserRepository extends ReactiveCrudRepository<UserPrincipal, Long> {

    Mono<UserPrincipal> findByUsername(String username);

}
