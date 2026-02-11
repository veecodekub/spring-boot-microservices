package dev.veecodekub.orderservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import dev.veecodekub.orderservice.enumerate.PaymentMethod;
import dev.veecodekub.orderservice.enumerate.PaymentStatus;

public record PaymentSnapshot(
        Long id,
        String orderId,
        BigDecimal amount,
        String currency,
        PaymentMethod method,
        PaymentStatus status,
        LocalDateTime createdAt) {

}
