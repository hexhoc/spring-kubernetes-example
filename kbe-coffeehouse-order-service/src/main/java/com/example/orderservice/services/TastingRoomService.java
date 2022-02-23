package com.example.orderservice.services;

import com.example.orderservice.bootstrap.OrderServiceBootstrap;
import com.example.orderservice.dto.CoffeeDto;
import com.example.orderservice.entity.CoffeeOrder;
import com.example.orderservice.entity.CoffeeOrderLine;
import com.example.orderservice.entity.Customer;
import com.example.orderservice.model.CoffeePagedList;
import com.example.orderservice.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TastingRoomService {
    private final CoffeeOrderManager coffeeOrderManager;
    private final CustomerRepository customerRepository;
    private final CoffeeService coffeeService;

    public void createTastingRoomOrder(){

        getRandomCoffee().ifPresent(coffeeId -> {

            Customer customer = customerRepository.findByCustomerName(OrderServiceBootstrap.CUSTOMER_NAME).orElseThrow();

            CoffeeOrder coffeeOrder = CoffeeOrder.builder().customer(customer).build();

            CoffeeOrderLine line = CoffeeOrderLine.builder()
                    .coffeeId(coffeeId)
                    .coffeeOrder(coffeeOrder)
                    .orderQuantity(new Random().nextInt(5) + 1) //zero based
                    .build();

            Set<CoffeeOrderLine> lines = new HashSet<>(1);
            lines.add(line);

            coffeeOrder.setCoffeeOrderLines(lines);

            coffeeOrderManager.newCoffeeOrder(coffeeOrder);
        });
    }

    private Optional<UUID> getRandomCoffee(){

        Optional<CoffeePagedList> listOptional = coffeeService.getListofCoffees();

        if (listOptional.isPresent()) {
            CoffeePagedList coffeePagedList = listOptional.get();

            if (coffeePagedList.getContent() != null && coffeePagedList.getContent().size() > 0) {
                List<CoffeeDto> dtoList = coffeePagedList.getContent();

                int k = new Random().nextInt(dtoList.size());

                return Optional.of(dtoList.get(k).getId());
            }
        }

        log.debug("Failed to get list of coffees");

        return Optional.empty();

    }
}
