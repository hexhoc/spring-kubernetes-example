package com.example.coffeeservice.dto.mapper;

import com.example.coffeeservice.dto.CoffeeDto;
import com.example.coffeeservice.entity.Coffee;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

// Invoke external mappers using uses method in @Mapper annotation,
// to convert timestamp from Coffee to OffsetDateTime in CoffeeDto
@Mapper(uses = DateMapper.class)
// Override mapping logic for coffeeToCoffeeDtoWithInventory method, using Decorator
@DecoratedWith(CoffeeMapperDecorator.class)
public interface CoffeeMapper {

    CoffeeDto coffeeToCoffeeDto(Coffee coffee);

    CoffeeDto coffeeToCoffeeDtoWithInventory(Coffee coffee);

    Coffee coffeeDtoToCoffee(CoffeeDto coffeeDto);
}
