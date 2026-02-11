package dev.veecodekub.orderservice.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

import dev.veecodekub.orderservice.enumerate.OrderStatus;
import dev.veecodekub.orderservice.enumerate.PaymentMethod;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrderResponse(
        UUID id,
        Long customerId,
        OrderStatus status,
        BigDecimal total,
        List<OrderItemResponse> items,
        PaymentMethod paymentMethod,
        String paymentCurrency) {

    public record OrderItemResponse(
            Long productId,
            String productName,
            BigDecimal productPrice,
            Integer quantity,
            BigDecimal total) {
    }
}
