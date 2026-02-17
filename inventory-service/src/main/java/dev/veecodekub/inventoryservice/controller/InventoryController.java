package dev.veecodekub.inventoryservice.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.veecodekub.inventoryservice.dto.InventoryRequest;
import dev.veecodekub.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/inventories")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    public void addInventory(@RequestBody InventoryRequest request) {
        inventoryService.addInventory(request);
    }

    @GetMapping("/product/{productId}/available-qty")
    public Integer getAvailableQtyByProductId(@PathVariable Long productId) {
        return inventoryService.getAvailableQtyByProductId(productId);
    }

    @GetMapping("/stocks/batch")
    public Map<Long, Integer> getStockBatch(@RequestParam List<Long> productIds) {
        return inventoryService.getStockBatch(productIds);
    }

    @PostMapping("/reserve/order/{orderId}")
    public void reserveStockForOrder(@PathVariable UUID orderId) {
        inventoryService.reserveStockForOrder(orderId);
    }

    @PostMapping("/release/order/{orderId}")
    public void releaseStockForOrder(@PathVariable UUID orderId) {
        inventoryService.releaseStockForOrder(orderId);
    }
}
