package dev.veecodekub.orderservice.dto;

import java.math.BigDecimal;
import java.util.List;

import dev.veecodekub.orderservice.enumerate.PaymentMethod;

public record CreateOrderRequest(
        List<OrderItemRequest> items,
        PaymentMethod paymentMethod,
        String paymentCurrency) {

    public record OrderItemRequest(
            Long productId,
            String productName,
            BigDecimal productPrice,
            Integer quantity) {
    }
}
