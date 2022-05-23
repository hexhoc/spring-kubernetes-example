package com.example.coffeeservice.service.order;

import com.example.coffeeservice.config.kafka.KafkaTopicConfig;
import com.example.coffeeservice.model.events.CoffeeOrderValidationResult;
import com.example.coffeeservice.model.events.ValidateCoffeeOrderRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CoffeeOrderValidationListener {

    private final CoffeeOrderValidator coffeeOrderValidator;

    private final KafkaTemplate<String, CoffeeOrderValidationResult> kafkaTemplate;

    // Get message from order-service and send message back to order-service
    @KafkaListener(
            topics = KafkaTopicConfig.VALIDATE_ORDER_QUEUE,
            groupId = "coffee-service-group",
            containerFactory = "kafkaJsonListenerContainerFactory")
    public void listen(ValidateCoffeeOrderRequest event) {

        Boolean orderIsValid = coffeeOrderValidator.validateOrder(event.getCoffeeOrder());

        log.debug("Validation Result for Order Id: " + event.getCoffeeOrder() + " is: " + orderIsValid);

        kafkaTemplate.send(KafkaTopicConfig.VALIDATE_ORDER_RESULT_QUEUE, CoffeeOrderValidationResult.builder()
                .coffeeOrderId(event.getCoffeeOrder().getId())
                .isValid(orderIsValid)
                .build());

    }
}
