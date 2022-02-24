package com.example.coffeeservice.bootstrap;

import com.example.coffeeservice.entity.Coffee;
import com.example.coffeeservice.entity.Coffeehouse;
import com.example.coffeeservice.model.CoffeeStyleEnum;
import com.example.coffeeservice.repository.CoffeeRepository;
import com.example.coffeeservice.repository.CoffeehouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultCoffeehouseLoader implements CommandLineRunner {

    public static final String COFFEE_1_UPC = "0631234200036";
    public static final String COFFEE_2_UPC = "0631234300019";
    public static final String COFFEE_3_UPC = "0083783375213";
    public static final String COFFEE_4_UPC = "0083783375232";
    public static final String COFFEE_5_UPC = "0083782275213";

    private final CoffeehouseRepository coffeehouseRepository;
    private final CoffeeRepository coffeeRepository;
    private final CacheManager cacheManager;


    @Override
    public void run(String... args) throws Exception {
        log.debug("Initializing Data");

        loadCoffeehouseData();
        loadCoffeeData();

        cacheManager.getCache("coffeeListCache").clear();
        log.debug("Data Initialized. Coffee Records loaded {}", coffeeRepository.count());
    }



    private void loadCoffeeData() {

        coffeeRepository.deleteAll();
        coffeeRepository.flush();

        Coffee mangoBobs = Coffee.builder()
                .coffeeName("Mango Bobs")
                .coffeeStyle(CoffeeStyleEnum.AMERICANO)
                .minOnHand(12)
                .quantityToBrew(200)
                .quantityOnHand(500)
                .upc(COFFEE_1_UPC)
                .build();

        coffeeRepository.save(mangoBobs);

        Coffee galaxyCat = Coffee.builder()
                .coffeeName("Galaxy Cat")
                .coffeeStyle(CoffeeStyleEnum.AMERICANO)
                .minOnHand(12)
                .quantityToBrew(200)
                .upc(COFFEE_2_UPC)
                .build();

        coffeeRepository.save(galaxyCat);

        Coffee pinball = Coffee.builder()
                .coffeeName("Pinball Porter")
                .coffeeStyle(CoffeeStyleEnum.CAPPUCCINO)
                .minOnHand(12)
                .quantityToBrew(200)
                .upc(COFFEE_3_UPC)
                .build();

        coffeeRepository.save(pinball);

        coffeeRepository.save(Coffee.builder()
                .coffeeName("Golden Buddha")
                .coffeeStyle(CoffeeStyleEnum.ESPRESSO)
                .minOnHand(12)
                .quantityToBrew(300)
                .upc(COFFEE_4_UPC)
                .build());

        coffeeRepository.save(Coffee.builder()
                .coffeeName("Cage Blond")
                .coffeeStyle(CoffeeStyleEnum.LATTE)
                .minOnHand(12)
                .quantityToBrew(200)
                .upc(COFFEE_5_UPC)
                .build());


    }

    private void loadCoffeehouseData() {
        if (coffeehouseRepository.count() == 0) {
            coffeehouseRepository.save(Coffeehouse
                    .builder()
                    .coffeehouseName("Cage coffeehouse")
                    .build());
        }
    }

}
