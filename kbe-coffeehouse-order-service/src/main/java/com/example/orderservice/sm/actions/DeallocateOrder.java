package com.example.orderservice.sm.actions;

import com.example.orderservice.config.JmsConfig;
import com.example.orderservice.entity.CoffeeOrder;
import com.example.orderservice.entity.CoffeeOrderEventEnum;
import com.example.orderservice.entity.CoffeeOrderStatusEnum;
import com.example.orderservice.model.events.DeAllocateOrderRequest;
import com.example.orderservice.web.mappers.CoffeeOrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import static com.example.orderservice.services.CoffeeOrderManagerImpl.ORDER_OBJECT_HEADER;

@Slf4j
@RequiredArgsConstructor
@Component
public class DeallocateOrder implements Action<CoffeeOrderStatusEnum, CoffeeOrderEventEnum> {
    private final JmsTemplate jmsTemplate;
    private final CoffeeOrderMapper coffeeOrderMapper;

    @Override
    public void execute(StateContext<CoffeeOrderStatusEnum, CoffeeOrderEventEnum> context) {
        CoffeeOrder coffeeOrder = context.getStateMachine().getExtendedState()
                .get(ORDER_OBJECT_HEADER, CoffeeOrder.class);


        jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_QUEUE, DeAllocateOrderRequest
                .builder()
                .coffeeOrder(coffeeOrderMapper.coffeeOrderToDto(coffeeOrder))
                .build());

        log.debug("Sent request to queue" + JmsConfig.DEALLOCATE_ORDER_QUEUE + "for Coffee Order Id: " + coffeeOrder.getId().toString());
    }
}
