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
public class AllocateCoffeeOrderResult {
    private CoffeeOrderDto coffeeOrderDto;
    private Boolean allocationError = false;
    private Boolean pendingInventory = false;
}
