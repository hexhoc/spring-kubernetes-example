package com.example.coffeeservice.service.inventory;

import com.example.coffeeservice.config.FeignClientConfig;
import com.example.coffeeservice.dto.CoffeeInventoryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.UUID;

@Profile({"local-discovery", "digitalocean"})
@FeignClient(name = "inventory-service", fallback = InventoryFailoverService.class, configuration = FeignClientConfig.class)
public interface InventoryServiceFeignClient {

    @RequestMapping(method = RequestMethod.GET, value = CoffeeInventoryServiceRestTemplateImpl.INVENTORY_PATH)
    ResponseEntity<List<CoffeeInventoryDto>> getOnhandInventory(@PathVariable UUID coffeeId);
}
