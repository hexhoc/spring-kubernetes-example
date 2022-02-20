package com.example.coffeeservice.model.events;

import com.example.coffeeservice.dto.CoffeeDto;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@NoArgsConstructor
public class BrewCoffeeEvent extends CoffeeEvent implements Serializable {

    static final long serialVersionUID = 5294557463904704401L;

    public BrewCoffeeEvent(CoffeeDto coffeeDto) {
        super(coffeeDto);
    }

}
