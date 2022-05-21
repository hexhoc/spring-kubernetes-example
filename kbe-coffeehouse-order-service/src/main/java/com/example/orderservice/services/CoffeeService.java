package com.example.orderservice.services;

import com.example.orderservice.dto.CoffeeDto;
import com.example.orderservice.model.CoffeePagedList;
import java.util.Optional;
import java.util.UUID;

public interface CoffeeService {

    Optional<CoffeeDto> getCoffeeById(UUID uuid);

    Optional<CoffeeDto> getCoffeeByUpc(String upc);

    Optional<CoffeePagedList> getListofCoffee();
}
