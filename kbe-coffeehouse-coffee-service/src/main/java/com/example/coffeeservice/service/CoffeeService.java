package com.example.coffeeservice.service;

import com.example.coffeeservice.model.Coffee;
import com.example.coffeeservice.model.CoffeePagedList;
import com.example.coffeeservice.model.CoffeeStyleEnum;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

public interface CoffeeService {
    CoffeePagedList listCoffee(String coffeeName, CoffeeStyleEnum coffeeStyle, PageRequest pageRequest, Boolean showInventoryOnHand);

    Coffee findBeerById(UUID coffeeId, Boolean showInventoryOnHand);

    Coffee saveCoffee(Coffee coffee);

    void updateCoffee(UUID beerId, Coffee coffee);

    void deleteById(UUID coffeeId);

    Coffee findCoffeeByUpc(String upc);
}

