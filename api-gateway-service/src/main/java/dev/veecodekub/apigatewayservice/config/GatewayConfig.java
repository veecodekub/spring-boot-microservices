package dev.veecodekub.apigatewayservice.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r.path("/auth/**")
                        .uri("lb://auth-service"))
                .route("user-service", r -> r.path("/users/**")
                        .uri("lb://user-service"))
                .route("product-service", r -> r.path("/products/**")
                        .uri("lb://product-service"))
                .route("inventory-service", r -> r.path("/inventories/**")
                        .uri("lb://inventory-service"))
                .route("order-service", r -> r.path("/orders/**")
                        .uri("lb://order-service"))
                .route("payment-service", r -> r.path("/payments/**")
                        .uri("lb://payment-service"))
                .build();
    }
}
