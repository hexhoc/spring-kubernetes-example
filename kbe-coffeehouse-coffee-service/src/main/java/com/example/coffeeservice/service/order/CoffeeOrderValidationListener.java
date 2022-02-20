package com.example.coffeeservice.service.order;

import com.example.coffeeservice.config.JmsConfig;
import com.example.coffeeservice.model.events.CoffeeOrderValidationResult;
import com.example.coffeeservice.model.events.ValidateCoffeeOrderRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CoffeeOrderValidationListener {

    private final CoffeeOrderValidator coffeeOrderValidator;
    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.VALIDATE_ORDER_QUEUE)
    public void listen(ValidateCoffeeOrderRequest event){

        Boolean orderIsValid = coffeeOrderValidator.validateOrder(event.getCoffeeOrder());

        log.debug("Validation Result for Order Id: " + event.getCoffeeOrder() + " is: " + orderIsValid);

        jmsTemplate.convertAndSend(JmsConfig.VALIDATE_ORDER_RESULT_QUEUE, CoffeeOrderValidationResult.builder()
                .coffeeOrderId(event.getCoffeeOrder().getId())
                .isValid(orderIsValid)
                .build());
    }
}
