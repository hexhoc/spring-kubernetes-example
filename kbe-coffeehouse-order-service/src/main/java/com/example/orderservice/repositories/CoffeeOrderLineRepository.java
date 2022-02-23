package com.example.orderservice.repositories;

import com.example.orderservice.entity.CoffeeOrderLine;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface CoffeeOrderLineRepository extends PagingAndSortingRepository<CoffeeOrderLine, UUID> {
}
