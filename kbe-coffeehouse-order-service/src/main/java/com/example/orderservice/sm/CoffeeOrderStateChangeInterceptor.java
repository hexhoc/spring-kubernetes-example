package com.example.orderservice.sm;

import com.example.orderservice.entity.CoffeeOrder;
import com.example.orderservice.entity.CoffeeOrderEventEnum;
import com.example.orderservice.entity.CoffeeOrderStatusEnum;
import com.example.orderservice.repositories.CoffeeOrderRepository;
import com.example.orderservice.services.CoffeeOrderManagerImpl;
import com.example.orderservice.web.mappers.DateMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class CoffeeOrderStateChangeInterceptor extends StateMachineInterceptorAdapter<CoffeeOrderStatusEnum, CoffeeOrderEventEnum> {

    private final CoffeeOrderRepository coffeeOrderRepository;
    private final RestTemplateBuilder restTemplateBuilder;
    private final DateMapper dateMapper = new DateMapper();

    @Transactional
    @Override
    public void preStateChange(
            State<CoffeeOrderStatusEnum, CoffeeOrderEventEnum> state,
            Message<CoffeeOrderEventEnum> message,
            Transition<CoffeeOrderStatusEnum, CoffeeOrderEventEnum> transition,
            StateMachine<CoffeeOrderStatusEnum, CoffeeOrderEventEnum> stateMachine,
            StateMachine<CoffeeOrderStatusEnum, CoffeeOrderEventEnum> rootStateMachine) {
        Optional.ofNullable(message)
                .flatMap(msg -> Optional.ofNullable((String) msg.getHeaders().getOrDefault(CoffeeOrderManagerImpl.ORDER_ID_HEADER, " ")))
                .ifPresent(orderId -> {
            log.debug("Saving state for order id: " + orderId + " Status: " + state.getId());

            CoffeeOrder coffeeOrder = coffeeOrderRepository.getOne(UUID.fromString(orderId));
            coffeeOrder.setOrderStatus(state.getId());
            coffeeOrderRepository.saveAndFlush(coffeeOrder);
        });
    }

//    @Override
//    public void postStateChange(State<CoffeeOrderStatusEnum, CoffeeOrderEventEnum> state, Message<CoffeeOrderEventEnum> message, Transition<CoffeeOrderStatusEnum, CoffeeOrderEventEnum> transition, StateMachine<CoffeeOrderStatusEnum, CoffeeOrderEventEnum> stateMachine) {
//        log.debug("Post State Change");
//
//        CoffeeOrder coffeeOrder = stateMachine.getExtendedState()
//                .get(ORDER_OBJECT_HEADER, CoffeeOrder.class);
//
//        try{
//            if (coffeeOrder.getOrderStatusCallbackUrl() != null) {
//
//                OrderStatusUpdate update = OrderStatusUpdate.builder()
//                        .id(coffeeOrder.getId())
//                        .orderId(coffeeOrder.getId())
//                        .version(coffeeOrder.getVersion() != null ? coffeeOrder.getVersion().intValue() : null)
//                        .createdDate(dateMapper.asOffsetDateTime(coffeeOrder.getCreatedDate()))
//                        .lastModifiedDate(dateMapper.asOffsetDateTime(coffeeOrder.getLastModifiedDate()))
//                        .orderStatus(coffeeOrder.getOrderStatus() != null ? coffeeOrder.getOrderStatus().toString() : null)
//                        .customerRef(coffeeOrder.getCustomerRef())
//                        .build();
//
//                log.debug("Posting to callback url");
//                RestTemplate restTemplate = restTemplateBuilder.build();
//                restTemplate.postForObject(coffeeOrder.getOrderStatusCallbackUrl(), update, String.class);
//            }
//        } catch (Throwable t){
//            log.error("Error Preforming callback for order: " + coffeeOrder.getId(), t);
//        }
//
//        log.debug("Post State change complete");
//    }
}
