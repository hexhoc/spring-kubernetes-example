package com.example.coffeeservice.model.events;

import com.example.coffeeservice.dto.CoffeeDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoffeeEvent {

    private CoffeeDto coffeeDto;
}
