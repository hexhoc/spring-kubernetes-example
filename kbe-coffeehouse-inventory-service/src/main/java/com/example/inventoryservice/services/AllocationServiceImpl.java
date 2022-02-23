package com.example.inventoryservice.services;

import com.example.inventoryservice.dto.CoffeeOrderDto;
import com.example.inventoryservice.dto.CoffeeOrderLineDto;
import com.example.inventoryservice.entity.CoffeeInventory;
import com.example.inventoryservice.repositories.CoffeeInventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor
@Service
public class AllocationServiceImpl implements AllocationService {

    private final CoffeeInventoryRepository coffeeInventoryRepository;

    @Override
    public Boolean allocateOrder(CoffeeOrderDto coffeeOrderDto) {
        log.debug("Allocating OrderId: " + coffeeOrderDto.getId());

        AtomicInteger totalOrdered = new AtomicInteger();
        AtomicInteger totalAllocated = new AtomicInteger();

        coffeeOrderDto.getCoffeeOrderLines().forEach(coffeeOrderLine -> {
            if ((((coffeeOrderLine.getOrderQuantity() != null ? coffeeOrderLine.getOrderQuantity() : 0)
                    - (coffeeOrderLine.getQuantityAllocated() != null ? coffeeOrderLine.getQuantityAllocated() : 0)) > 0)) {
                allocateCoffeeOrderLine(coffeeOrderLine);
            }
            totalOrdered.set(totalOrdered.get() + coffeeOrderLine.getOrderQuantity());
            totalAllocated.set(totalAllocated.get() + (coffeeOrderLine.getQuantityAllocated() != null ? coffeeOrderLine.getQuantityAllocated() : 0));
        });

        log.debug("Total Ordered: " + totalOrdered.get() + " Total Allocated: " + totalAllocated.get());

        return totalOrdered.get() == totalAllocated.get();
    }

    private void allocateCoffeeOrderLine(CoffeeOrderLineDto coffeeOrderLine) {
        List<CoffeeInventory> coffeeInventoryList = coffeeInventoryRepository.findAllByUpc(coffeeOrderLine.getUpc());

        coffeeInventoryList.forEach(coffeeInventory -> {
            int inventory = (coffeeInventory.getQuantityOnHand() == null) ? 0 : coffeeInventory.getQuantityOnHand();
            int orderQty = (coffeeOrderLine.getOrderQuantity() == null) ? 0 : coffeeOrderLine.getOrderQuantity();
            int allocatedQty = (coffeeOrderLine.getQuantityAllocated() == null) ? 0 : coffeeOrderLine.getQuantityAllocated();
            int qtyToAllocate = orderQty - allocatedQty;

            if (inventory >= qtyToAllocate) { // full allocation
                inventory = inventory - qtyToAllocate;
                coffeeOrderLine.setQuantityAllocated(orderQty);
                coffeeInventory.setQuantityOnHand(inventory);

                coffeeInventoryRepository.save(coffeeInventory);
            } else if (inventory > 0) { //partial allocation
                coffeeOrderLine.setQuantityAllocated(allocatedQty + inventory);
                coffeeInventory.setQuantityOnHand(0);

                coffeeInventoryRepository.delete(coffeeInventory);
            }
        });

    }
}
