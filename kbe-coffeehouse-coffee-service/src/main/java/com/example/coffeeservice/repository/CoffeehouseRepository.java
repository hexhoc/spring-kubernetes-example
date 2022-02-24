package com.example.coffeeservice.repository;

import com.example.coffeeservice.entity.Coffeehouse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CoffeehouseRepository extends JpaRepository<Coffeehouse, UUID> {
}
