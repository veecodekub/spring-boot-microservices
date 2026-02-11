package dev.veecodekub.paymentservice.dto;

import java.math.BigDecimal;
import java.util.UUID;

import dev.veecodekub.paymentservice.enumerate.PaymentMethod;
import dev.veecodekub.paymentservice.enumerate.PaymentStatus;

public record PaymentResponse(
        Long id,
        UUID orderId,
        BigDecimal amount,
        String currency,
        PaymentMethod method,
        PaymentStatus status) {

}
