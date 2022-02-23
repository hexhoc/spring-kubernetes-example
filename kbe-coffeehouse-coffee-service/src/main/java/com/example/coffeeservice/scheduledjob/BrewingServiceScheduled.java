package com.example.coffeeservice.scheduledjob;

import com.example.coffeeservice.service.brewing.BrewingService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class BrewingServiceScheduled {

    private final BrewingService brewingService;

    @Scheduled(fixedRate = 5000) //run every 5 seconds
    public void checkForLowInventory() {
        log.debug("Start sheduled check for low inventory");
        brewingService.checkForLowInventory();
    }
}
