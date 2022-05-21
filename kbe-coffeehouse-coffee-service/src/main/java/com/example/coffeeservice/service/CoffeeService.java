package com.example.coffeeservice.service;

import com.example.coffeeservice.dto.CoffeeDto;
import com.example.coffeeservice.model.CoffeePagedList;
import com.example.coffeeservice.model.CoffeeStyleEnum;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

public interface CoffeeService {
    CoffeePagedList listCoffee(String coffeeName, CoffeeStyleEnum coffeeStyle, PageRequest pageRequest, Boolean showInventoryOnHand);

    CoffeeDto findCoffeeById(UUID coffeeId,  Boolean showInventoryOnHand);

    CoffeeDto saveCoffee(CoffeeDto coffeeDto);

    void updateCoffee(UUID coffeeId, CoffeeDto coffeeDto);

    void deleteById(UUID coffeeId);

    CoffeeDto findCoffeeByUpc(String upc);
}

