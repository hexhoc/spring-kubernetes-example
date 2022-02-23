package com.example.orderservice.web.mappers;

import com.example.orderservice.dto.CoffeeDto;
import com.example.orderservice.dto.CoffeeOrderLineDto;
import com.example.orderservice.entity.CoffeeOrderLine;
import com.example.orderservice.services.CoffeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Optional;

public abstract class CoffeeOrderLineMapperDecorator implements CoffeeOrderLineMapper {

    private CoffeeService coffeeService;
    private CoffeeOrderLineMapper coffeeOrderLineMapper;

    @Autowired
    public void setCoffeeService(CoffeeService coffeeService) {
        this.coffeeService = coffeeService;
    }

    @Autowired
    @Qualifier("delegate")
    public void setCoffeeOrderLineMapper(CoffeeOrderLineMapper coffeeOrderLineMapper) {
        this.coffeeOrderLineMapper = coffeeOrderLineMapper;
    }

    @Override
    public CoffeeOrderLineDto coffeeOrderLineToDto(CoffeeOrderLine line) {
        CoffeeOrderLineDto orderLineDto = coffeeOrderLineMapper.coffeeOrderLineToDto(line);
        Optional<CoffeeDto> coffeeDtoOptional = coffeeService.getCoffeeById(line.getCoffeeId());

        coffeeDtoOptional.ifPresent(coffeeDto -> {
            orderLineDto.setCoffeeName(coffeeDto.getCoffeeName());
            orderLineDto.setCoffeeStyle(coffeeDto.getCoffeeName());
            orderLineDto.setUpc(coffeeDto.getUpc());
            orderLineDto.setPrice(coffeeDto.getPrice());
        });

        return orderLineDto;
    }
}
