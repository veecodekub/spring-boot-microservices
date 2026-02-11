package dev.veecodekub.inventoryservice.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import dev.veecodekub.inventoryservice.enumerate.OrderStatus;
import dev.veecodekub.inventoryservice.enumerate.PaymentMethod;

public record OrderSnapshot(
        UUID id,
        Long customerId,
        OrderStatus status,
        BigDecimal total,
        List<OrderItemSnapshot> items,
        PaymentMethod paymentMethod,
        String paymentCurrency) {

    public record OrderItemSnapshot(
            Long productId,
            String productName,
            BigDecimal productPrice,
            Integer quantity,
            BigDecimal total) {
    }
}
