package com.example.orderservice.sm.actions;

import com.example.orderservice.config.JmsConfig;
import com.example.orderservice.entity.CoffeeOrder;
import com.example.orderservice.entity.CoffeeOrderEventEnum;
import com.example.orderservice.entity.CoffeeOrderStatusEnum;
import com.example.orderservice.model.events.ValidateCoffeeOrderRequest;
import com.example.orderservice.web.mappers.CoffeeOrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import static com.example.orderservice.services.CoffeeOrderManagerImpl.ORDER_OBJECT_HEADER;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValidateCoffeeOrder implements Action<CoffeeOrderStatusEnum, CoffeeOrderEventEnum> {

    private final JmsTemplate jmsTemplate;
    private final CoffeeOrderMapper coffeeOrderMapper;

    // Send message to coffee-service
    @Override
    public void execute(StateContext<CoffeeOrderStatusEnum, CoffeeOrderEventEnum> stateContext) {
        CoffeeOrder coffeeOrder = stateContext.getStateMachine().getExtendedState()
                .get(ORDER_OBJECT_HEADER, CoffeeOrder.class);

        jmsTemplate.convertAndSend(JmsConfig.VALIDATE_ORDER_QUEUE, ValidateCoffeeOrderRequest
                .builder()
                .coffeeOrder(coffeeOrderMapper.coffeeOrderToDto(coffeeOrder))
                .build());

        log.debug("Sent request to queue" + JmsConfig.VALIDATE_ORDER_QUEUE + "for Coffee Order Id: " + coffeeOrder.getId().toString());
    }
}
