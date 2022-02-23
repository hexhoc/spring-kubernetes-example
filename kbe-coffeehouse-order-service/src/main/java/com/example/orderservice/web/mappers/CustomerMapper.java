package com.example.orderservice.web.mappers;

import com.example.orderservice.dto.CustomerDto;
import com.example.orderservice.entity.Customer;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class})
public interface CustomerMapper {
    CustomerDto customerToDto(Customer customer);

    Customer dtoToCustomer(Customer dto);
}
