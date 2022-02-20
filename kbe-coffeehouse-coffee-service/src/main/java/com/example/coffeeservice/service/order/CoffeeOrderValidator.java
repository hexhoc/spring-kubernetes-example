package com.example.coffeeservice.service.order;

import com.example.coffeeservice.dto.CoffeeOrderDto;
import com.example.coffeeservice.repository.CoffeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;


@Slf4j
@RequiredArgsConstructor
@Component
public class CoffeeOrderValidator {

    private final CoffeeRepository coffeeRepository;

    public Boolean validateOrder(CoffeeOrderDto coffeeOrderDto){

        AtomicInteger coffeesNotFound = new AtomicInteger();

        coffeeOrderDto.getCoffeeOrderLines().forEach(coffeeOrderLineDto -> {
            if (coffeeRepository.findByUpc(coffeeOrderLineDto.getUpc()) == null){
                coffeesNotFound.incrementAndGet();
            }
        });

        //fail order if UPC not found
        return coffeesNotFound.get() == 0;
    }
}
