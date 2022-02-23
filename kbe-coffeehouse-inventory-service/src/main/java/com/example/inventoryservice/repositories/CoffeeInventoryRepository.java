package com.example.inventoryservice.repositories;


import com.example.inventoryservice.entity.CoffeeInventory;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

public interface CoffeeInventoryRepository extends PagingAndSortingRepository<CoffeeInventory, UUID> {

    List<CoffeeInventory> findAllByCoffeeId(UUID coffeeId);

    List<CoffeeInventory> findAllByUpc(String upc);
}
