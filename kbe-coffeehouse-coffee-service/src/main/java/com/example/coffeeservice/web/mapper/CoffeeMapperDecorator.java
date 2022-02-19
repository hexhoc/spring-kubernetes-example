package com.example.coffeeservice.web.mapper;

import com.example.coffeeservice.dto.CoffeeDto;
import com.example.coffeeservice.model.Coffee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class CoffeeMapperDecorator implements CoffeeMapper {
    //TODO Add CoffeeInventoryService
    private CoffeeMapper mapper;

    @Autowired
    @Qualifier("delegate")
    public void setMapper(CoffeeMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public CoffeeDto coffeeToCoffeeDtoWithInventory(Coffee coffee) {
        CoffeeDto dto = mapper.coffeeToCoffeeDto(coffee);
//        dto.setQuantityOnHand(coffeeInventoryService.getOnhandInventory(coffee.getId()));
        return dto;
    }

}
