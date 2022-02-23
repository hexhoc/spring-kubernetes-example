package com.example.orderservice.services;

import com.example.orderservice.dto.CoffeeOrderDto;
import com.example.orderservice.entity.CoffeeOrder;
import com.example.orderservice.entity.CoffeeOrderEventEnum;
import com.example.orderservice.entity.CoffeeOrderStatusEnum;
import com.example.orderservice.repositories.CoffeeOrderRepository;
import com.example.orderservice.sm.CoffeeOrderStateChangeInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor
@Service
public class CoffeeOrderManagerImpl implements CoffeeOrderManager {

    public static final String ORDER_ID_HEADER = "ORDER_ID";
    public static final String ORDER_OBJECT_HEADER = "COFFEE_ORDER";

    private final StateMachineFactory<CoffeeOrderStatusEnum, CoffeeOrderEventEnum> stateMachineFactory;
    private final CoffeeOrderRepository coffeeOrderRepository;
    private final CoffeeOrderStateChangeInterceptor coffeeOrderStateChangeInterceptor;
    private final EntityManager entityManager;

    @Transactional
    @Override
    public CoffeeOrder newCoffeeOrder(CoffeeOrder coffeeOrder) {
        log.debug("New Order: " + coffeeOrder.toString());

        coffeeOrder.setOrderStatus(CoffeeOrderStatusEnum.NEW);
        CoffeeOrder savedOrder = coffeeOrderRepository.saveAndFlush(coffeeOrder);

        //send validation event
        sendCoffeeOrderEvent(savedOrder, CoffeeOrderEventEnum.VALIDATE_ORDER);

        return savedOrder;
    }

    @Transactional
    @Override
    public void coffeeOrderPassedValidation(UUID coffeeOrderId) {

        log.debug("Order Passed Validation:" + coffeeOrderId);

        awaitForStatus(coffeeOrderId, CoffeeOrderStatusEnum.PENDING_VALIDATION);

        coffeeOrderRepository.findById(coffeeOrderId).ifPresentOrElse(coffeeOrder -> {
            sendCoffeeOrderEvent(coffeeOrder, CoffeeOrderEventEnum.VALIDATION_PASSED);

            awaitForStatus(coffeeOrderId, CoffeeOrderStatusEnum.VALIDATED);

            CoffeeOrder validatedOrder = coffeeOrderRepository.findById(coffeeOrderId).get();

            sendCoffeeOrderEvent(validatedOrder, CoffeeOrderEventEnum.ALLOCATE_ORDER);
        }, () -> log.error("Order Not Found. Id: " + coffeeOrderId));
    }

    public void awaitForStatus(UUID coffeeOrderId, CoffeeOrderStatusEnum statusEnum) {

        AtomicBoolean found = new AtomicBoolean(false);
        AtomicInteger loopCount = new AtomicInteger(0);

        while (!found.get()) {
            if (loopCount.incrementAndGet() > 20) {
                found.set(true);
                log.debug("Loop Retries exceeded");
            }

            coffeeOrderRepository.findById(coffeeOrderId).ifPresentOrElse(coffeeOrder -> {
                if (coffeeOrder.getOrderStatus().equals(statusEnum)) {
                    found.set(true);
                    log.debug("Order Found");
                } else {
                    log.debug("Order Status Not Equal. Expected: " + statusEnum.name() + " Found: " + coffeeOrder.getOrderStatus().name());
                }
            }, () -> {
                log.debug("Order Id Not Found");
            });

            if(!found.get()){
                log.debug("Try via String id");
                coffeeOrderRepository.findOrderUsingStringId(coffeeOrderId.toString()).ifPresentOrElse(coffeeOrder -> {
                    found.set(true);
                },() -> log.debug("Not found via string id"));
            }

            if (!found.get()) {
                try {
                    log.debug("Sleeping for retry");
                    Thread.sleep(200);
                } catch (Exception e) {
                    // do nothing
                }
            }
        }
    }

    @Transactional
    @Override
    public void coffeeOrderFailedValidation(UUID coffeeOrderId) {

        awaitForStatus(coffeeOrderId, CoffeeOrderStatusEnum.PENDING_VALIDATION);

        coffeeOrderRepository.findById(coffeeOrderId).ifPresentOrElse(coffeeOrder -> {
            sendCoffeeOrderEvent(coffeeOrder, CoffeeOrderEventEnum.VALIDATION_FAILED);
        }, () -> log.error("Order Not Found. Id: " + coffeeOrderId));
    }

    @Override
    public void coffeeOrderAllocationPassed(CoffeeOrderDto coffeeOrderDto) {

        log.debug("Allocation Passed: " + coffeeOrderDto.toString());

        coffeeOrderRepository.findById(coffeeOrderDto.getId()).ifPresentOrElse(coffeeOrder -> {
            sendCoffeeOrderEvent(coffeeOrder, CoffeeOrderEventEnum.ALLOCATION_SUCCESS);
            awaitForStatus(coffeeOrder.getId(), CoffeeOrderStatusEnum.ALLOCATED);
            updateAllocatedQty(coffeeOrderDto, coffeeOrder);
        }, () -> log.error("Order Not Found. Id: " + coffeeOrderDto.getId()));
    }

    @Override
    public void coffeeOrderAllocationPendingInventory(CoffeeOrderDto coffeeOrderDto) {
        coffeeOrderRepository.findById(coffeeOrderDto.getId()).ifPresentOrElse(coffeeOrder -> {
            sendCoffeeOrderEvent(coffeeOrder, CoffeeOrderEventEnum.ALLOCATION_NO_INVENTORY);
            awaitForStatus(coffeeOrder.getId(), CoffeeOrderStatusEnum.PENDING_INVENTORY);
            updateAllocatedQty(coffeeOrderDto, coffeeOrder);
        }, () -> log.error("Order Not Found. Id: " + coffeeOrderDto.getId()));
    }

    private void updateAllocatedQty(CoffeeOrderDto coffeeOrderDto, CoffeeOrder coffeeOrder) {
        CoffeeOrder allocatedOrder = coffeeOrderRepository.getOne(coffeeOrderDto.getId());

        allocatedOrder.getCoffeeOrderLines().forEach(coffeeOrderLine -> {
            coffeeOrderDto.getCoffeeOrderLines().forEach(coffeeOrderLineDto -> {
                if (coffeeOrderLine.getId().equals(coffeeOrderLineDto.getId())) {
                    coffeeOrderLine.setQuantityAllocated(coffeeOrderLineDto.getQuantityAllocated());
                }
            });
        });

        coffeeOrderRepository.saveAndFlush(allocatedOrder);
    }

    @Override
    public void coffeeOrderAllocationFailed(CoffeeOrderDto coffeeOrderDto) {
        coffeeOrderRepository.findById(coffeeOrderDto.getId()).ifPresentOrElse(coffeeOrder -> {
            sendCoffeeOrderEvent(coffeeOrder, CoffeeOrderEventEnum.ALLOCATION_FAILED);
        }, () -> log.error("Order Not Found. Id: " + coffeeOrderDto.getId()));
    }

    @Override
    public void pickupCoffeeOrder(UUID coffeeOrderId) {
        coffeeOrderRepository.findById(coffeeOrderId).ifPresentOrElse(coffeeOrder -> {
            sendCoffeeOrderEvent(coffeeOrder, CoffeeOrderEventEnum.COFFEE_ORDER_PICKED_UP);
        }, () -> log.error("Order Not Found. Id: " + coffeeOrderId));
    }

    @Override
    public void cancelOrder(UUID coffeeOrderId) {
        coffeeOrderRepository.findById(coffeeOrderId).ifPresentOrElse(coffeeOrder -> {
            sendCoffeeOrderEvent(coffeeOrder, CoffeeOrderEventEnum.CANCEL_ORDER);
        }, () -> log.error("Order Not Found. Id: " + coffeeOrderId));
    }

    private void sendCoffeeOrderEvent(CoffeeOrder coffeeOrder, CoffeeOrderEventEnum event) {

        StateMachine<CoffeeOrderStatusEnum, CoffeeOrderEventEnum> sm = build(coffeeOrder);

        Message msg = MessageBuilder.withPayload(event)
                .setHeader(ORDER_ID_HEADER, coffeeOrder.getId().toString())
                .build();

        sm.sendEvent(msg);
    }

    private StateMachine<CoffeeOrderStatusEnum, CoffeeOrderEventEnum> build(CoffeeOrder coffeeOrder) {

        StateMachine<CoffeeOrderStatusEnum, CoffeeOrderEventEnum> sm = stateMachineFactory.getStateMachine(coffeeOrder.getId());

        sm.stop();

        sm.getStateMachineAccessor()
                .doWithAllRegions(sma -> {
                    sma.addStateMachineInterceptor(coffeeOrderStateChangeInterceptor);
                    sma.resetStateMachine(new DefaultStateMachineContext<>(coffeeOrder.getOrderStatus(), null,
                            null, null));
                });

        sm.getExtendedState().getVariables().put(ORDER_OBJECT_HEADER, coffeeOrder);

        sm.start();

        return sm;
    }
}
