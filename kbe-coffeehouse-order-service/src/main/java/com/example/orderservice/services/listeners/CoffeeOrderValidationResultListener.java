package com.example.orderservice.services.listeners;

import com.example.orderservice.config.JmsConfig;
import com.example.orderservice.model.events.CoffeeOrderValidationResult;
import com.example.orderservice.services.CoffeeOrderManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class CoffeeOrderValidationResultListener {

    private final CoffeeOrderManager coffeeOrderManager;

    // Get message from order-service
    @JmsListener(destination = JmsConfig.VALIDATE_ORDER_RESULT_QUEUE)
    public void listen(CoffeeOrderValidationResult result) {
        final UUID coffeeOrderId = result.getCoffeeOrderId();

        log.debug("Validation Result for Order Id: " + coffeeOrderId + " is: " + result.getIsValid());

        if(result.getIsValid()){
            coffeeOrderManager.coffeeOrderPassedValidation(coffeeOrderId);
        } else {
            coffeeOrderManager.coffeeOrderFailedValidation(coffeeOrderId);
        }
    }
}
