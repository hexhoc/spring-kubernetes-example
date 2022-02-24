package com.example.inventoryfailover.web;

import com.example.inventoryfailover.model.CoffeeInventoryDto;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
public class InventoryHandler {
    public Mono<ServerResponse> listInventory(ServerRequest request){
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_STREAM_JSON)
                .body(Mono.just(Arrays.asList(
                        CoffeeInventoryDto.builder()
                                .id(UUID.randomUUID())
                                .coffeeId(UUID.fromString("00000000-0000-0000-0000-000000000000"))
                                .quantityOnHand(999)
                                .createdDate(OffsetDateTime.now())
                                .lastModifiedDate(OffsetDateTime.now())
                                .build())), List.class);
    }
}
