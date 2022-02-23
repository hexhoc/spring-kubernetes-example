package com.example.orderservice.model.events;

import com.example.orderservice.dto.CoffeeOrderDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllocateCoffeeOrderResult {
    private CoffeeOrderDto coffeeOrderDto;
    private Boolean allocationError;
    private Boolean pendingInventory;
}
