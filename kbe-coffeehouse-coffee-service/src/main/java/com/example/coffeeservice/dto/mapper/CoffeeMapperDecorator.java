package com.example.coffeeservice.dto.mapper;

import com.example.coffeeservice.dto.CoffeeDto;
import com.example.coffeeservice.entity.Coffee;
import com.example.coffeeservice.service.inventory.CoffeeInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class CoffeeMapperDecorator implements CoffeeMapper {

    private CoffeeInventoryService coffeeInventoryService;
    private CoffeeMapper mapper;


    @Autowired
    public void setCoffeeInventoryService(CoffeeInventoryService coffeeInventoryService) {
        this.coffeeInventoryService = coffeeInventoryService;
    }

    @Autowired
    // When using @DecoratedWith on a mapper with component model spring,
    // the generated implementation of the original mapper is annotated with the Spring annotation @Qualifier("delegate")
    // To autowire that bean in your decorator, add that qualifier annotation as well
    @Qualifier("delegate")
    public void setMapper(CoffeeMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public CoffeeDto coffeeToCoffeeDtoWithInventory(Coffee coffee) {
        CoffeeDto coffeeDto = mapper.coffeeToCoffeeDto(coffee);
        coffeeDto.setQuantityOnHand(coffeeInventoryService.getOnhandInventory(coffee.getId()));
        return coffeeDto;
    }

}
