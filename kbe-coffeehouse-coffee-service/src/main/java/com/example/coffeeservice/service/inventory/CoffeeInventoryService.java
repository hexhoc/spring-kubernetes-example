package com.example.coffeeservice.service.inventory;

import java.util.UUID;

public interface CoffeeInventoryService {

    Integer getOnhandInventory(UUID coffeeId);
}
