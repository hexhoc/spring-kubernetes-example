package com.example.inventoryservice.services;

import com.example.inventoryservice.dto.CoffeeOrderDto;

public interface AllocationService {

    Boolean allocateOrder(CoffeeOrderDto coffeeOrderDto);
}
