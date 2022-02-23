package com.example.coffeeservice.web.controller;

import com.example.coffeeservice.dto.CoffeeDto;
import com.example.coffeeservice.entity.Coffee;
import com.example.coffeeservice.model.CoffeePagedList;
import com.example.coffeeservice.model.CoffeeStyleEnum;
import com.example.coffeeservice.service.CoffeeService;
import com.example.coffeeservice.web.mapper.CoffeeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
@RestController
public class CoffeeController {
    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 25;

    private final CoffeeService coffeeService;
    private final CoffeeMapper coffeeMapper;

    @GetMapping(produces = { "application/json" }, path = "coffee")
    public ResponseEntity<CoffeePagedList> listCoffee(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                     @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                     @RequestParam(value = "coffeeName", required = false) String coffeeName,
                                                     @RequestParam(value = "coffeeStyle", required = false) CoffeeStyleEnum coffeeStyle,
                                                     @RequestParam(value = "showInventoryOnHand", required = false) Boolean showInventoryOnHand){

        log.debug("Listing Coffees");

        if (showInventoryOnHand == null) {
            showInventoryOnHand = false;
        }

        if (pageNumber == null || pageNumber < 0){
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        Page<Coffee> coffeePage = coffeeService.findAll(coffeeName, coffeeStyle, PageRequest.of(pageNumber, pageSize), showInventoryOnHand);
        CoffeePagedList coffeePagedList;

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

        return new ResponseEntity<>(coffeePagedList, HttpStatus.OK);
    }

    @GetMapping(path = {"coffee/{coffeeId}"}, produces = { "application/json" })
    public ResponseEntity<CoffeeDto> getCoffeeById(@PathVariable("coffeeId") UUID coffeeId,
                                                  @RequestParam(value = "showInventoryOnHand", required = false) Boolean showInventoryOnHand){

        log.debug("Get Request for CoffeeId: " + coffeeId);

        if (showInventoryOnHand == null) {
            showInventoryOnHand = false;
        }

        CoffeeDto coffeeDto;
        Coffee coffee = coffeeService.findById(coffeeId, showInventoryOnHand);
        if(showInventoryOnHand) {
            coffeeDto = coffeeMapper.coffeeToCoffeeDtoWithInventory(coffee);
        } else {
            coffeeDto = coffeeMapper.coffeeToCoffeeDto(coffee);
        }

        return new ResponseEntity<>(coffeeDto, HttpStatus.OK);
    }

    @GetMapping(path = {"coffeeUpc/{upc}"}, produces = { "application/json" })
    public ResponseEntity<CoffeeDto>  getCoffeeByUpc(@PathVariable("upc") String upc){
        return new ResponseEntity<>(coffeeMapper.coffeeToCoffeeDto(coffeeService.findByUpc(upc)), HttpStatus.OK);
    }

    @PostMapping(path = "coffee")
    public ResponseEntity saveNewCoffee(@Valid @RequestBody CoffeeDto coffeeDto){

        Coffee coffee = coffeeService.save(coffeeMapper.coffeeDtoToCoffee(coffeeDto));

        HttpHeaders httpHeaders = new HttpHeaders();

        //todo hostname for uri
        httpHeaders.add("Location", "/api/v1/coffee_service/" + coffee.getId().toString());

        return new ResponseEntity(httpHeaders, HttpStatus.CREATED);
    }

    @PutMapping(path = {"coffee/{coffeeId}"}, produces = { "application/json" })
    public ResponseEntity updateCoffee(@PathVariable("coffeeId") UUID coffeeId, @Valid @RequestBody CoffeeDto coffeeDto){

        coffeeService.update(coffeeId, coffeeMapper.coffeeDtoToCoffee(coffeeDto));

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping({"coffee/{coffeeId}"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCoffee(@PathVariable("coffeeId") UUID coffeeId){
        coffeeService.deleteById(coffeeId);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<List> badRequestHandler(ConstraintViolationException e){
        List<String> errors = new ArrayList<>(e.getConstraintViolations().size());

        e.getConstraintViolations().forEach(constraintViolation -> {
            errors.add(constraintViolation.getPropertyPath().toString() + " : " + constraintViolation.getMessage());
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

}
