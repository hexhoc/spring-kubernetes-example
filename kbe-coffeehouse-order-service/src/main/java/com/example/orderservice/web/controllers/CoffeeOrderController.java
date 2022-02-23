package com.example.orderservice.web.controllers;

import com.example.orderservice.dto.CoffeeOrderDto;
import com.example.orderservice.dto.CoffeeOrderPagedList;
import com.example.orderservice.services.CoffeeOrderService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/api/v1/customers/{customerId}/")
@RestController
public class CoffeeOrderController {

    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 25;

    private final CoffeeOrderService coffeeOrderService;

    public CoffeeOrderController(CoffeeOrderService coffeeOrderService) {
        this.coffeeOrderService = coffeeOrderService;
    }

    @GetMapping("orders")
    public CoffeeOrderPagedList listOrders(@PathVariable("customerId") UUID customerId,
                                           @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                           @RequestParam(value = "pageSize", required = false) Integer pageSize){

        if (pageNumber == null || pageNumber < 0){
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        return coffeeOrderService.listOrders(customerId, PageRequest.of(pageNumber, pageSize));
    }

    @PostMapping("orders")
    @ResponseStatus(HttpStatus.CREATED)
    public CoffeeOrderDto placeOrder(@PathVariable("customerId") UUID customerId, @RequestBody CoffeeOrderDto coffeeOrderDto){
        return coffeeOrderService.placeOrder(customerId, coffeeOrderDto);
    }

    @GetMapping("orders/{orderId}")
    public CoffeeOrderDto getOrder(@PathVariable("customerId") UUID customerId, @PathVariable("orderId") UUID orderId){
        return coffeeOrderService.getOrderById(customerId, orderId);
    }

    @PutMapping("/orders/{orderId}/pickup")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void pickupOrder(@PathVariable("customerId") UUID customerId, @PathVariable("orderId") UUID orderId){
        coffeeOrderService.pickupOrder(customerId, orderId);
    }
}
