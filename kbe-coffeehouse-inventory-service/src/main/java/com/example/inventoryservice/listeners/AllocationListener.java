package com.example.inventoryservice.listeners;

import com.example.inventoryservice.config.JmsConfig;
import com.example.inventoryservice.model.events.AllocateCoffeeOrderRequest;
import com.example.inventoryservice.model.events.AllocateCoffeeOrderResult;
import com.example.inventoryservice.services.AllocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class AllocationListener {

    private final AllocationService allocationService;
    private final JmsTemplate jmsTemplate;

    // Get message from order-service and send result back to order-service
    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_QUEUE)
    public void listen(AllocateCoffeeOrderRequest request){
        log.debug("Allocating Order: " + request.getCoffeeOrder().getId());

        AllocateCoffeeOrderResult.AllocateCoffeeOrderResultBuilder builder = AllocateCoffeeOrderResult.builder();
        builder.coffeeOrderDto(request.getCoffeeOrder());

        try {
            Boolean allocationResult = allocationService.allocateOrder(request.getCoffeeOrder());

            if (allocationResult){
                builder.pendingInventory(false);
            } else {
                builder.pendingInventory(true);
            }

            builder.allocationError(false);
        } catch (Exception e) {
            //some error occured
            builder.allocationError(true).pendingInventory(false);
            log.error("Allocation attempt failed for order id " + request.getCoffeeOrder().getId(), e);
        }

        jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_RESULT_QUEUE, builder.build());
    }
}
