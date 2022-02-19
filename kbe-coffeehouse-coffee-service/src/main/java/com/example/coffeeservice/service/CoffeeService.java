package com.example.coffeeservice.service;

import com.example.coffeeservice.model.Coffee;
import com.example.coffeeservice.model.CoffeePagedList;
import com.example.coffeeservice.model.CoffeeStyleEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

public interface CoffeeService {
    Page<Coffee> findAll(String coffeeName, CoffeeStyleEnum coffeeStyle, PageRequest pageRequest, Boolean showInventoryOnHand);

    Coffee findById(UUID coffeeId, Boolean showInventoryOnHand);

    Coffee save(Coffee coffee);

    void update(UUID beerId, Coffee coffee);

    void deleteById(UUID coffeeId);

    Coffee findByUpc(String upc);
}

