package com.example.inventoryservice.web.mappers;

import com.example.inventoryservice.dto.CoffeeInventoryDto;
import com.example.inventoryservice.entity.CoffeeInventory;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class})
public interface CoffeeInventoryMapper {

    CoffeeInventory coffeeInventoryDtoToCoffeeInventory(CoffeeInventoryDto coffeeInventoryDTO);

    CoffeeInventoryDto coffeeInventoryToCoffeeInventoryDto(CoffeeInventory coffeeInventory);
}
