package com.example.coffeeservice.service.brewing;

import com.example.coffeeservice.config.JmsConfig;
import com.example.coffeeservice.dto.CoffeeDto;
import com.example.coffeeservice.entity.Coffee;
import com.example.coffeeservice.model.events.BrewCoffeeEvent;
import com.example.coffeeservice.model.events.NewInventoryEvent;
import com.example.coffeeservice.repository.CoffeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class BrewCoffeeListener {

    private final JmsTemplate jmsTemplate;
    private final KafkaTemplate<String, NewInventoryEvent> kafkaTemplate;
    private final CoffeeRepository coffeeRepository;

    // Coffee-service get message from itself and send message to inventory-service
    // After brew coffee we should increase amount in inventory-service
    @Transactional
    @JmsListener(destination = JmsConfig.BREWING_REQUEST_QUEUE)
    public void listen(BrewCoffeeEvent brewCoffeeEvent){
        NewInventoryEvent newInventoryEvent = brewCoffee(brewCoffeeEvent);
        jmsTemplate.convertAndSend(JmsConfig.NEW_INVENTORY_QUEUE, newInventoryEvent);
    }

    @Transactional
    @KafkaListener(
            topics = JmsConfig.BREWING_REQUEST_QUEUE,
            groupId = "coffee-service-group",
            containerFactory = "kafkaJsonListenerContainerFactory")
    public void kafkaListener(BrewCoffeeEvent brewCoffeeEvent) {
        log.warn("BrewCoffeeListener get brewCoffeeEvent");
        NewInventoryEvent newInventoryEvent = brewCoffee(brewCoffeeEvent);
        kafkaTemplate.send(JmsConfig.NEW_INVENTORY_QUEUE, newInventoryEvent);
    }

    private NewInventoryEvent brewCoffee(BrewCoffeeEvent brewCoffeeEvent) {
        CoffeeDto dto = brewCoffeeEvent.getCoffeeDto();

        Coffee coffee = coffeeRepository.findById(dto.getId()).get();
        //Brewing some coffee
        dto.setQuantityOnHand(coffee.getQuantityToBrew());

        return new NewInventoryEvent(dto);
    }

}
