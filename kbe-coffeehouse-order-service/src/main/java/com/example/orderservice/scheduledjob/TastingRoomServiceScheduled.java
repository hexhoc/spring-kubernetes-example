package com.example.orderservice.scheduledjob;

import com.example.orderservice.services.TastingRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class TastingRoomServiceScheduled {
    private final TastingRoomService tastingRoomService;

    @Scheduled(fixedRateString = "${custom.tasting.room.rate}")
    public void createTastingRoomOrder() {
        log.debug("Start tasting");
        tastingRoomService.createTastingRoomOrder();
    }
}
