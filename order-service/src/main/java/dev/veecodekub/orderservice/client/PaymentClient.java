package dev.veecodekub.orderservice.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import dev.veecodekub.orderservice.dto.PaymentSnapshot;

@FeignClient(name = "payment-service", path = "/payments")
public interface PaymentClient {

    @GetMapping("/order/{orderId}")
    PaymentSnapshot getPaymentByOrderId(@PathVariable UUID orderId);
}
