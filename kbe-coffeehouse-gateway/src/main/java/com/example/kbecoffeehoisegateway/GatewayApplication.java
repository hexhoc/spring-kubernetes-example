package com.example.kbecoffeehoisegateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {

        return builder.routes()
                .route("coffee-service", r -> r.path("/api/v1/coffee/*", "/api/v1/coffeeUpc/*")
                        .uri("http://coffee-service:8080"))
                .route("inventory-service", r -> r.path("/api/v1/coffee/*/inventory")
                        .uri("http://inventory-service:8082"))
                .route("order-service", r -> r.path(("/api/v1/customers/**"))
                        .uri("http://order-service:8081"))
                .build();
    }
}
