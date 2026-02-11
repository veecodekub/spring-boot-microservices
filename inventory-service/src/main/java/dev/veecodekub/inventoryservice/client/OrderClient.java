package dev.veecodekub.inventoryservice.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import dev.veecodekub.inventoryservice.dto.OrderSnapshot;

@FeignClient(name = "order-service", path = "/orders")
public interface OrderClient {

    @GetMapping("/{orderId}")
    public OrderSnapshot getOrderByOrderId(@PathVariable UUID orderId);
}
