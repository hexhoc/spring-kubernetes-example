package com.example.coffeeservice.service;

import com.example.coffeeservice.entity.Coffee;
import com.example.coffeeservice.model.CoffeeStyleEnum;
import com.example.coffeeservice.repository.CoffeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoffeeServiceImpl implements CoffeeService{

    private final CoffeeRepository coffeeRepository;

    @Cacheable(cacheNames = "coffeeListCache", condition = "#showInventoryOnHand == false ")
    @Override
    public Page<Coffee> findAll(String coffeeName, CoffeeStyleEnum coffeeStyle, PageRequest pageRequest, Boolean showInventoryOnHand) {
        //TODO remake this method using JpaSpecification

        log.debug("Listing coffee");

        Page<Coffee> coffeePage;

        if (!StringUtils.isEmpty(coffeeName) && !StringUtils.isEmpty(coffeeStyle)) {
            //search both
            coffeePage = coffeeRepository.findAllByCoffeeNameAndCoffeeStyle(coffeeName, coffeeStyle, pageRequest);
        } else if (!StringUtils.isEmpty(coffeeName) && StringUtils.isEmpty(coffeeStyle)) {
            //search coffee_service name
            coffeePage = coffeeRepository.findAllByCoffeeName(coffeeName, pageRequest);
        } else if (StringUtils.isEmpty(coffeeName) && !StringUtils.isEmpty(coffeeStyle)) {
            //search coffee_service style
            coffeePage = coffeeRepository.findAllByCoffeeStyle(coffeeStyle, pageRequest);
        } else {
            coffeePage = coffeeRepository.findAll(pageRequest);
        }

        return coffeePage;
    }

    @Cacheable(cacheNames = "coffeeCache", key = "#coffeeId", condition = "#showInventoryOnHand == false ")
    @Override
    public Coffee findById(UUID coffeeId, Boolean showInventoryOnHand) {
        log.debug("Finding coffee by id: " + coffeeId);

        Optional<Coffee> coffeeOptional = coffeeRepository.findById(coffeeId);

        if (coffeeOptional.isPresent()) {
            log.debug("Found CoffeeId: " + coffeeId);
            return coffeeOptional.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not Found. UUID: " + coffeeId);
        }
    }

    @Override
    public Coffee save(Coffee coffee) {
        return coffeeRepository.save(coffee);
    }

    @Override
    public void update(UUID coffeeId, Coffee changedCoffee) {
        Optional<Coffee> coffeeOptional = coffeeRepository.findById(coffeeId);

        coffeeOptional.ifPresentOrElse(coffee -> {
            coffee.setCoffeeName(changedCoffee.getCoffeeName());
            coffee.setCoffeeStyle(changedCoffee.getCoffeeStyle());
            coffee.setPrice(changedCoffee.getPrice());
            coffee.setUpc(changedCoffee.getUpc());
            coffeeRepository.save(coffee);
        }, () -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not Found. UUID: " + coffeeId);
        });
    }

    @Override
    public void deleteById(UUID coffeeId) {
        coffeeRepository.deleteById(coffeeId);
    }

    @Override
    public Coffee findByUpc(String upc) {
        return coffeeRepository.findByUpc(upc);
    }
}
