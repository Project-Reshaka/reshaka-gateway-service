package ru.reshaka.gateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.reshaka.gateway.infra.postgre.repo.UserRepository;

@Configuration
@RequiredArgsConstructor
public class GatewayConfig {

    @Value("${gateway.service.taskbackend.url}")
    private String taskBackendUrl;

    @Value("${gateway.service.usercontrollerbackend.url}")
    private String userControllerBackendUrl;

    public @Bean RouteLocator routes(RouteLocatorBuilder builder) {


        return builder.routes()
                .route(r -> r
                        .path("/api/tasks/**")
                        .filters(f -> f
                                .stripPrefix(1)
                        )
                        .uri(taskBackendUrl)
                )
                .route(r -> r
                        .path("/api/admin/**", "/api/stats/**", "/api/analytics/**", "/api/other/**")
                        .filters(f -> f
                                .stripPrefix(1)
                        )
                        .uri(userControllerBackendUrl))
                .build();
    }
}
