package com.example.inventoryservice.model.events;

import com.example.inventoryservice.dto.CoffeeOrderDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllocateCoffeeOrderRequest {
    private CoffeeOrderDto coffeeOrder;
}
