package com.example.inventoryservice.listeners;

import com.example.inventoryservice.config.JmsConfig;
import com.example.inventoryservice.entity.CoffeeInventory;
import com.example.inventoryservice.model.events.NewInventoryEvent;
import com.example.inventoryservice.repositories.CoffeeInventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class NewInventoryListener {

    private final CoffeeInventoryRepository coffeeInventoryRepository;

    // Get message from coffee-service
    @JmsListener(destination = JmsConfig.NEW_INVENTORY_QUEUE)
    public void listen(NewInventoryEvent event){
        log.debug(event.toString());

        coffeeInventoryRepository.save(CoffeeInventory.builder()
                .coffeeId(event.getCoffeeDto().getId())
                .quantityOnHand(event.getCoffeeDto().getQuantityOnHand())
                .upc(event.getCoffeeDto().getUpc())
                .build());
    }

    @Transactional
    @KafkaListener(
            topics = JmsConfig.NEW_INVENTORY_QUEUE,
            groupId = "inventory-service-group",
            containerFactory = "kafkaJsonListenerContainerFactory")
    public void kafkaListener(NewInventoryEvent newInventoryEvent) {
        log.debug(newInventoryEvent.toString());

        coffeeInventoryRepository.save(CoffeeInventory.builder()
                .coffeeId(newInventoryEvent.getCoffeeDto().getId())
                .quantityOnHand(newInventoryEvent.getCoffeeDto().getQuantityOnHand())
                .upc(newInventoryEvent.getCoffeeDto().getUpc())
                .build());
    }

}
