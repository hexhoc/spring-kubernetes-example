package com.example.orderservice.services.listeners;

import com.example.orderservice.config.JmsConfig;
import com.example.orderservice.model.events.AllocateCoffeeOrderResult;
import com.example.orderservice.services.CoffeeOrderManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CoffeeOrderAllocationResultListener {
    private final CoffeeOrderManager coffeeOrderManager;

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_RESULT_QUEUE)
    public void listen(AllocateCoffeeOrderResult result) {

        if(!result.getAllocationError() && !result.getPendingInventory()){
            //allocated normally
            coffeeOrderManager.coffeeOrderAllocationPassed(result.getCoffeeOrderDto());
        } else if(!result.getAllocationError() && result.getPendingInventory()) {
            //pending inventory
            coffeeOrderManager.coffeeOrderAllocationPendingInventory(result.getCoffeeOrderDto());
        } else if(result.getAllocationError()){
            //allocation error
            coffeeOrderManager.coffeeOrderAllocationFailed(result.getCoffeeOrderDto());
        }
    }
}
