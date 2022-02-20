package com.example.coffeeservice.service.inventory;

import com.example.coffeeservice.dto.CoffeeInventoryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by jt on 3/4/20.
 */
@Slf4j
@RequiredArgsConstructor
@Profile({"local-discovery", "digitalocean"})
@Service
public class CoffeeInventoryServiceFeign implements CoffeeInventoryService {
    private final InventoryServiceFeignClient inventoryServiceFeignClient;

    @Override
    public Integer getOnhandInventory(UUID coffeeId) {
        log.debug("Calling Inventory Service w/Feign - CoffeeId: " + coffeeId);

        int onHand = 0;

        try {
            ResponseEntity<List<CoffeeInventoryDto>> responseEntity = inventoryServiceFeignClient.getOnhandInventory(coffeeId);

            if (responseEntity.getBody() != null && responseEntity.getBody().size() > 0) {
                log.debug("Inventory found, summing inventory");

                onHand = Objects.requireNonNull(responseEntity.getBody())
                        .stream()
                        .mapToInt(CoffeeInventoryDto::getQuantityOnHand)
                        .sum();
            }
        } catch (Exception e) {
            log.error("Exception thrown calling inventory service", e);
            throw e;
        }

        log.debug("CoffeeId: " + coffeeId + " On hand is: " + onHand);

        return onHand;
    }
}
