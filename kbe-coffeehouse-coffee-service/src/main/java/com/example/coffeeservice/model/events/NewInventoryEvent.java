package com.example.coffeeservice.model.events;

import com.example.coffeeservice.dto.CoffeeDto;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
public class NewInventoryEvent extends CoffeeEvent implements Serializable {

    static final long serialVersionUID = -1761313326070018802L;

    public NewInventoryEvent(CoffeeDto coffeeDto) {
        super(coffeeDto);
    }
}
