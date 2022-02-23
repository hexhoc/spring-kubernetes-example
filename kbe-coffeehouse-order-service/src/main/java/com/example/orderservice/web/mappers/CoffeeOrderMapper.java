package com.example.orderservice.web.mappers;

import com.example.orderservice.dto.CoffeeOrderDto;
import com.example.orderservice.entity.CoffeeOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {DateMapper.class, CoffeeOrderLineMapper.class})
public interface CoffeeOrderMapper {

    @Mapping(source = "customer.id", target = "customerId")
    CoffeeOrderDto coffeeOrderToDto(CoffeeOrder coffeeOrder);

    CoffeeOrder dtoToCoffeeOrder(CoffeeOrderDto dto);

}
