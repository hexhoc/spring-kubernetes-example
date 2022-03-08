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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class BrewCoffeeListener {

    private final JmsTemplate jmsTemplate;
    private final CoffeeRepository coffeeRepository;

    // Coffee-service get message from it self and send message to inventory-service 
    // After brew coffee we should increase amount in inventory-service
    @Transactional
    @JmsListener(destination = JmsConfig.BREWING_REQUEST_QUEUE)
    public void listen(BrewCoffeeEvent brewCoffeeEvent){

        CoffeeDto dto = brewCoffeeEvent.getCoffeeDto();

        Coffee coffee = coffeeRepository.findById(dto.getId()).get();
        //Brewing some coffee
        dto.setQuantityOnHand(coffee.getQuantityToBrew());

        NewInventoryEvent newInventoryEvent = new NewInventoryEvent(dto);

        jmsTemplate.convertAndSend(JmsConfig.NEW_INVENTORY_QUEUE, newInventoryEvent);
    }
}
