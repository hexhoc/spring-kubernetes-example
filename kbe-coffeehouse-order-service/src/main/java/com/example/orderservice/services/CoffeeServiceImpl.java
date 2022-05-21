package com.example.orderservice.services;

import com.example.orderservice.dto.CoffeeDto;
import com.example.orderservice.model.CoffeePagedList;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

@ConfigurationProperties(prefix = "custom.coffeehouse", ignoreUnknownFields = false)
@Service
public class CoffeeServiceImpl implements CoffeeService {

    public static final String COFFEE_PATH_V1 = "/api/v1/coffee/";
    public static final String COFFEE_UPC_PATH_V1 = "/api/v1/coffeeUpc/";
    private final RestTemplate restTemplate;

    private String coffeeServiceHost;

    public CoffeeServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public Optional<CoffeeDto> getCoffeeById(UUID uuid){
        return Optional.ofNullable(restTemplate.getForObject(coffeeServiceHost + COFFEE_PATH_V1 + uuid.toString(), CoffeeDto.class));
    }

    @Override
    public Optional<CoffeeDto> getCoffeeByUpc(String upc) {
        return Optional.ofNullable(restTemplate.getForObject(coffeeServiceHost + COFFEE_UPC_PATH_V1 + upc, CoffeeDto.class));
    }

    @Override
    public Optional<CoffeePagedList> getListofCoffee() {
        return Optional.ofNullable(restTemplate.getForObject(coffeeServiceHost + COFFEE_PATH_V1, CoffeePagedList.class));
    }

    public void setCoffeeServiceHost(String coffeeServiceHost) {
        this.coffeeServiceHost = coffeeServiceHost;
    }
}
