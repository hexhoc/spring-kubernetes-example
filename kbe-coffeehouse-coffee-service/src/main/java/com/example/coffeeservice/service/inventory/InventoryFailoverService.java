package com.example.coffeeservice.service.inventory;

import com.example.coffeeservice.dto.CoffeeInventoryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class InventoryFailoverService implements InventoryServiceFeignClient {
    private final InventoryFailoverFeignClient inventoryFailoverFeignClient;

    @Override
    public ResponseEntity<List<CoffeeInventoryDto>> getOnhandInventory(UUID coffeeId) {
        log.debug("Calling Inventory Failover for Coffee Id: " + coffeeId);
        return inventoryFailoverFeignClient.getOnhandInventory();
    }
}
