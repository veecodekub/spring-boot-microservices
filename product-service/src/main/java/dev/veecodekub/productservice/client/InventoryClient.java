package dev.veecodekub.productservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import dev.veecodekub.productservice.dto.CreateInventoryRequest;

@FeignClient(name = "inventory-service", path = "/inventories")
public interface InventoryClient {

    @PostMapping
    void addInventory(@RequestBody CreateInventoryRequest request);

    @GetMapping("/product/{productId}/available-qty")
    int getAvailableQtyByProductId(@PathVariable Long productId);
}
