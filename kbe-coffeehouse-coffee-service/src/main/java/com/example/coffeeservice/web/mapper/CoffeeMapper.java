package com.example.coffeeservice.web.mapper;

import com.example.coffeeservice.dto.CoffeeDto;
import com.example.coffeeservice.model.Coffee;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

//TODO Research mapstruct
@Mapper(uses = DateMapper.class)
@DecoratedWith(CoffeeMapperDecorator.class)
public interface CoffeeMapper {

    CoffeeDto coffeeToCoffeeDto(Coffee coffee);

    CoffeeDto coffeeToCoffeeDtoWithInventory(Coffee coffee);

    Coffee coffeeDtoToCoffee(CoffeeDto coffeeDto);
}
