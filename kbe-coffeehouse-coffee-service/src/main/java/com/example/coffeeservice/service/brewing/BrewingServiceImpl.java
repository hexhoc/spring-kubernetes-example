package com.example.coffeeservice.service.brewing;

import com.example.coffeeservice.config.JmsConfig;
import com.example.coffeeservice.model.Coffee;
import com.example.coffeeservice.model.events.BrewCoffeeEvent;
import com.example.coffeeservice.repository.CoffeeRepository;
import com.example.coffeeservice.service.inventory.CoffeeInventoryService;
import com.example.coffeeservice.web.mapper.CoffeeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrewingServiceImpl implements BrewingService {

    private final CoffeeInventoryService coffeeInventoryService;
    private final CoffeeRepository coffeeRepository;
    private final JmsTemplate jmsTemplate;
    private final CoffeeMapper coffeeMapper;

    @Override
    @Transactional
    @Scheduled(fixedRate = 5000) //run every 5 seconds
    public void checkForLowInventory() {
        log.debug("Checking Coffee Inventory");

        List<Coffee> coffees = coffeeRepository.findAll();

        coffees.forEach(coffee -> {

            Integer invQoh = coffeeInventoryService.getOnhandInventory(coffee.getId());

            if(coffee.getMinOnHand() >= invQoh ) {
                jmsTemplate.convertAndSend(JmsConfig.BREWING_REQUEST_QUEUE,
                        new BrewCoffeeEvent(coffeeMapper.coffeeToCoffeeDto(coffee)));
            }
        });
    }
}
