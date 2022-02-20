package com.example.coffeeservice.model.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoffeeOrderValidationResult {
    private Boolean isValid;
    private UUID coffeeOrderId;
}