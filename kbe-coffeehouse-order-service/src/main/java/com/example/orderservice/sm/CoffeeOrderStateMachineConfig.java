package com.example.orderservice.sm;

import com.example.orderservice.entity.CoffeeOrderEventEnum;
import com.example.orderservice.entity.CoffeeOrderStatusEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@RequiredArgsConstructor
@Configuration
@EnableStateMachineFactory
public class CoffeeOrderStateMachineConfig extends StateMachineConfigurerAdapter<CoffeeOrderStatusEnum, CoffeeOrderEventEnum> {

    private final Action<CoffeeOrderStatusEnum, CoffeeOrderEventEnum> validateCoffeeOrder;
    private final Action<CoffeeOrderStatusEnum, CoffeeOrderEventEnum> allocateCoffeeOrder;
    private final Action<CoffeeOrderStatusEnum, CoffeeOrderEventEnum> validationFailureAction;
    private final Action<CoffeeOrderStatusEnum, CoffeeOrderEventEnum> allocationFailureAction;
    private final Action<CoffeeOrderStatusEnum, CoffeeOrderEventEnum> deAllocateOrderAction;

    @Override
    public void configure(StateMachineStateConfigurer<CoffeeOrderStatusEnum, CoffeeOrderEventEnum> states) throws Exception {
        states.withStates()
                .initial(CoffeeOrderStatusEnum.NEW)
                .states(EnumSet.allOf(CoffeeOrderStatusEnum.class))
                .end(CoffeeOrderStatusEnum.PICKED_UP)
                .end(CoffeeOrderStatusEnum.DELIVERED)
                .end(CoffeeOrderStatusEnum.DELIVERY_EXCEPTION)
                .end(CoffeeOrderStatusEnum.VALIDATION_EXCEPTION)
                .end(CoffeeOrderStatusEnum.ALLOCATION_ERROR)
                .end(CoffeeOrderStatusEnum.CANCELLED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<CoffeeOrderStatusEnum, CoffeeOrderEventEnum> transitions) throws Exception {
        transitions.withExternal()
                .source(CoffeeOrderStatusEnum.NEW).target(CoffeeOrderStatusEnum.PENDING_VALIDATION)
                    .event(CoffeeOrderEventEnum.VALIDATE_ORDER)
                    .action(validateCoffeeOrder)
            .and().withExternal()
                .source(CoffeeOrderStatusEnum.PENDING_VALIDATION).target(CoffeeOrderStatusEnum.VALIDATED)
                    .event(CoffeeOrderEventEnum.VALIDATION_PASSED)
             //       .action(validationPassedAction)
            .and().withExternal()
                .source(CoffeeOrderStatusEnum.PENDING_VALIDATION).target(CoffeeOrderStatusEnum.CANCELLED)
                .event(CoffeeOrderEventEnum.CANCEL_ORDER)
            .and().withExternal()
                .source(CoffeeOrderStatusEnum.PENDING_VALIDATION).target(CoffeeOrderStatusEnum.VALIDATION_EXCEPTION)
                .event(CoffeeOrderEventEnum.VALIDATION_FAILED)
                .action(validationFailureAction)
            .and().withExternal()
                .source(CoffeeOrderStatusEnum.VALIDATED).target(CoffeeOrderStatusEnum.PENDING_ALLOCATION)
                .event(CoffeeOrderEventEnum.ALLOCATE_ORDER)
                .action(allocateCoffeeOrder)
            .and().withExternal()
                .source(CoffeeOrderStatusEnum.VALIDATED).target(CoffeeOrderStatusEnum.CANCELLED)
                .event(CoffeeOrderEventEnum.CANCEL_ORDER)
            .and().withExternal()
                .source(CoffeeOrderStatusEnum.PENDING_ALLOCATION).target(CoffeeOrderStatusEnum.ALLOCATED)
                .event(CoffeeOrderEventEnum.ALLOCATION_SUCCESS)
            .and().withExternal()
                .source(CoffeeOrderStatusEnum.PENDING_ALLOCATION).target(CoffeeOrderStatusEnum.CANCELLED)
                .event(CoffeeOrderEventEnum.CANCEL_ORDER)
            .and().withExternal()
                .source(CoffeeOrderStatusEnum.PENDING_ALLOCATION).target(CoffeeOrderStatusEnum.ALLOCATION_ERROR)
                .event(CoffeeOrderEventEnum.ALLOCATION_FAILED)
                .action(allocationFailureAction)
            .and().withExternal()
                .source(CoffeeOrderStatusEnum.PENDING_ALLOCATION).target(CoffeeOrderStatusEnum.PENDING_INVENTORY)
                .event(CoffeeOrderEventEnum.ALLOCATION_NO_INVENTORY)
            .and().withExternal()
                .source(CoffeeOrderStatusEnum.PENDING_ALLOCATION).target(CoffeeOrderStatusEnum.CANCELLED)
                .event(CoffeeOrderEventEnum.CANCEL_ORDER)
            .and().withExternal()
                .source(CoffeeOrderStatusEnum.ALLOCATED).target(CoffeeOrderStatusEnum.CANCELLED)
                .event(CoffeeOrderEventEnum.CANCEL_ORDER)
                .action(deAllocateOrderAction)
            .and().withExternal()
                .source(CoffeeOrderStatusEnum.ALLOCATED).target(CoffeeOrderStatusEnum.PICKED_UP)
                .event(CoffeeOrderEventEnum.COFFEE_ORDER_PICKED_UP)
        ;
    }
}
