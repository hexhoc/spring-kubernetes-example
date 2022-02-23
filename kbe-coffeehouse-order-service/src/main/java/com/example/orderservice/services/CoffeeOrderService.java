package com.example.orderservice.services;

import com.example.orderservice.dto.CoffeeOrderDto;
import com.example.orderservice.dto.CoffeeOrderPagedList;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CoffeeOrderService {
    CoffeeOrderPagedList listOrders(UUID customerId, Pageable pageable);

    CoffeeOrderDto placeOrder(UUID customerId, CoffeeOrderDto coffeeOrderDto);

    CoffeeOrderDto getOrderById(UUID customerId, UUID orderId);

    void pickupOrder(UUID customerId, UUID orderId);
}
