package dev.veecodekub.orderservice.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "inventory-service", path = "/inventories")
public interface InventoryClient {

    @PostMapping("/reserve/order/{orderId}")
    void reserve(@PathVariable UUID orderId);

    @PostMapping("/release/order/{orderId}")
    void release(@PathVariable UUID orderId);
}
