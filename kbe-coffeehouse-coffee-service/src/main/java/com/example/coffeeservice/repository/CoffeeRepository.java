package com.example.coffeeservice.repository;

import com.example.coffeeservice.entity.Coffee;
import com.example.coffeeservice.model.CoffeeStyleEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CoffeeRepository extends JpaRepository<Coffee, UUID> {
    
    Page<Coffee> findAllByCoffeeName(String coffeeName, Pageable pageable);

    Page<Coffee> findAllByCoffeeStyle(CoffeeStyleEnum coffeeStyle, Pageable pageable);

    Page<Coffee> findAllByCoffeeNameAndCoffeeStyle(String coffeeName, CoffeeStyleEnum coffeeStyle, Pageable pageable);

    Coffee findByUpc(String upc);
}
