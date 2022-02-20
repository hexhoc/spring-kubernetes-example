package com.example.coffeeservice.config;

import feign.auth.BasicAuthRequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfig {
    @Bean
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor(@Value("${custom.coffeehouse.inventory-user}") String user,
                @Value("${custom.coffeehouse.inventory-password}") String password) {


        return new BasicAuthRequestInterceptor(user, password);
    }
}
