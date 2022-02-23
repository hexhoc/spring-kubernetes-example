package com.example.orderservice.web.mappers;

import com.example.orderservice.dto.CoffeeOrderLineDto;
import com.example.orderservice.entity.CoffeeOrderLine;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class})
@DecoratedWith(CoffeeOrderLineMapperDecorator.class)
public interface CoffeeOrderLineMapper {
    CoffeeOrderLineDto coffeeOrderLineToDto(CoffeeOrderLine line);

    CoffeeOrderLine dtoToCoffeeOrderLine(CoffeeOrderLineDto dto);

}
