package com.example.coffeeservice.service.inventory;

import com.example.coffeeservice.dto.CoffeeInventoryDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

//TODO RESEARCH ALL INVENTORY CLASSES
@Profile("!local-discovery & !digitalocean")
@Slf4j
@ConfigurationProperties(prefix = "custom.coffeehouse", ignoreUnknownFields = true)
@Component
public class CoffeeInventoryServiceRestTemplateImpl implements CoffeeInventoryService {

    public static final String INVENTORY_PATH = "/api/v1/coffee/{coffeeId}/inventory";
    private final RestTemplate restTemplate;

    private String coffeeInventoryServiceHost;


    public void setCoffeeInventoryServiceHost(String coffeeInventoryServiceHost) {
        this.coffeeInventoryServiceHost = coffeeInventoryServiceHost;
    }


    public CoffeeInventoryServiceRestTemplateImpl(RestTemplateBuilder restTemplateBuilder, @Value("${custom.coffeehouse.inventory-user}") String inventoryUser,
                                                @Value("${custom.coffeehouse.inventory-password}") String inventoryPassword) {
        this.restTemplate = restTemplateBuilder.basicAuthentication(inventoryUser, inventoryPassword).build();
    }

    @Override
    public Integer getOnhandInventory(UUID coffeeId) {

        log.debug("Calling Inventory Service - CoffeeId: " + coffeeId);

        ResponseEntity<List<CoffeeInventoryDto>> responseEntity = restTemplate
                .exchange(coffeeInventoryServiceHost + INVENTORY_PATH, HttpMethod.GET, null,
                        new ParameterizedTypeReference<List<CoffeeInventoryDto>>(){}, (Object) coffeeId);

        Integer onHand = Objects.requireNonNull(responseEntity.getBody())
                .stream()
                .mapToInt(CoffeeInventoryDto::getQuantityOnHand)
                .sum();

        log.debug("CoffeeId: " + coffeeId + " On hand is: " + onHand);

        return onHand;
    }
}
