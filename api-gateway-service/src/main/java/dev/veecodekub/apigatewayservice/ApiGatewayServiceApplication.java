package dev.veecodekub.apigatewayservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayServiceApplication.class, args);
    }

    @Bean
    RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r.path("/auth/**")
                        .uri("lb://auth-service"))
                .route("user-service", r -> r.path("/users/**")
                        .uri("lb://user-service"))
                .route("product-service", r -> r.path("/products/**")
                        .filters(f -> f.tokenRelay())
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