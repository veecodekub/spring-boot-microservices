package dev.veecodekub.productservice.client;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import dev.veecodekub.productservice.dto.CreateInventoryRequest;

@FeignClient(name = "inventory-service", path = "/inventories")
public interface InventoryClient {

    @PostMapping
    void addInventory(@RequestBody CreateInventoryRequest request);

    @GetMapping("/product/{productId}/available-qty")
    int getAvailableQtyByProductId(@PathVariable Long productId);

    @GetMapping("/stocks/batch")
    Map<Long, Integer> getStockBatch(@RequestParam List<Long> productIds);
}
