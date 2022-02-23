package com.example.inventoryservice.web.controllers;

import com.example.inventoryservice.dto.CoffeeInventoryDto;
import com.example.inventoryservice.repositories.CoffeeInventoryRepository;
import com.example.inventoryservice.web.mappers.CoffeeInventoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
public class CoffeeInventoryController {

    private final CoffeeInventoryRepository coffeeInventoryRepository;
    private final CoffeeInventoryMapper coffeeInventoryMapper;

    @GetMapping("api/v1/coffee/{coffeeId}/inventory")
    List<CoffeeInventoryDto> listCoffeesById(@PathVariable UUID coffeeId){
        log.debug("Finding Inventory for coffeeId:" + coffeeId);

        return coffeeInventoryRepository.findAllByCoffeeId(coffeeId)
                .stream()
                .map(coffeeInventoryMapper::coffeeInventoryToCoffeeInventoryDto)
                .collect(Collectors.toList());
    }
}
