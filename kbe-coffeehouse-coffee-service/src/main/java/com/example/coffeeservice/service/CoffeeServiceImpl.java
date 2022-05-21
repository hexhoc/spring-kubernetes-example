package com.example.coffeeservice.service;

import com.example.coffeeservice.dto.CoffeeDto;
import com.example.coffeeservice.dto.mapper.CoffeeMapper;
import com.example.coffeeservice.entity.Coffee;
import com.example.coffeeservice.model.CoffeePagedList;
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
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoffeeServiceImpl implements CoffeeService {

    private final CoffeeRepository coffeeRepository;
    private final CoffeeMapper coffeeMapper;

    @Cacheable(cacheNames = "coffeeListCache", condition = "#showInventoryOnHand == false ")
    @Override
    public CoffeePagedList listCoffee(String coffeeName, CoffeeStyleEnum coffeeStyle, PageRequest pageRequest, Boolean showInventoryOnHand) {

        log.debug("Listing Coffee");

        CoffeePagedList coffeePagedList;
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

        if (showInventoryOnHand) {
            coffeePagedList = new CoffeePagedList(coffeePage
                    .getContent()
                    .stream()
                    .map(coffeeMapper::coffeeToCoffeeDtoWithInventory)
                    .collect(Collectors.toList()),
                    PageRequest
                            .of(coffeePage.getPageable().getPageNumber(),
                                    coffeePage.getPageable().getPageSize()),
                    coffeePage.getTotalElements());

        } else {
            coffeePagedList = new CoffeePagedList(coffeePage
                    .getContent()
                    .stream()
                    .map(coffeeMapper::coffeeToCoffeeDto)
                    .collect(Collectors.toList()),
                    PageRequest
                            .of(coffeePage.getPageable().getPageNumber(),
                                    coffeePage.getPageable().getPageSize()),
                    coffeePage.getTotalElements());
        }
        return coffeePagedList;
    }

    @Cacheable(cacheNames = "coffeeCache", key = "#coffeeId", condition = "#showInventoryOnHand == false ")
    @Override
    public CoffeeDto findCoffeeById(UUID coffeeId, Boolean showInventoryOnHand) {

        log.debug("Finding Coffee by id: " + coffeeId);

        Optional<Coffee> coffeeOptional = coffeeRepository.findById(coffeeId);

        if (coffeeOptional.isPresent()) {
            log.debug("Found CoffeeId: " + coffeeId);
            if(showInventoryOnHand) {
                return coffeeMapper.coffeeToCoffeeDtoWithInventory(coffeeOptional.get());
            } else {
                return coffeeMapper.coffeeToCoffeeDto(coffeeOptional.get());
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not Found. UUID: " + coffeeId);
        }
    }

    @Override
    public CoffeeDto saveCoffee(CoffeeDto coffeeDto) {
        return coffeeMapper.coffeeToCoffeeDto(coffeeRepository.save(coffeeMapper.coffeeDtoToCoffee(coffeeDto)));
    }

    @Override
    public void updateCoffee(UUID coffeeId, CoffeeDto coffeeDto) {
        Optional<Coffee> coffeeOptional = coffeeRepository.findById(coffeeId);

        coffeeOptional.ifPresentOrElse(coffee -> {
            coffee.setCoffeeName(coffeeDto.getCoffeeName());
            coffee.setCoffeeStyle(coffeeDto.getCoffeeStyle());
            coffee.setPrice(coffeeDto.getPrice());
            coffee.setUpc(coffeeDto.getUpc());
            coffeeRepository.save(coffee);
        }, () -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not Found. UUID: " + coffeeId);
        });
    }

    @Override
    public void deleteById(UUID coffeeId) {
        coffeeRepository.deleteById(coffeeId);
    }

    @Cacheable(cacheNames = "coffeeUpcCache")
    @Override
    public CoffeeDto findCoffeeByUpc(String upc) {
        return coffeeMapper.coffeeToCoffeeDto(coffeeRepository.findByUpc(upc));
    }
}
