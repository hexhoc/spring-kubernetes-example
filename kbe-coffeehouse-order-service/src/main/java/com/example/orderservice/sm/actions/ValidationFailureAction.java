package com.example.orderservice.sm.actions;

import com.example.orderservice.entity.CoffeeOrderEventEnum;
import com.example.orderservice.entity.CoffeeOrderStatusEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValidationFailureAction implements Action<CoffeeOrderStatusEnum, CoffeeOrderEventEnum> {
    @Override
    public void execute(StateContext<CoffeeOrderStatusEnum, CoffeeOrderEventEnum> context) {
        log.error("Validation Failed");
    }
}
