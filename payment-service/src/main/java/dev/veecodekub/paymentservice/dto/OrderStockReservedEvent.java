package dev.veecodekub.paymentservice.dto;

import java.math.BigDecimal;
import java.util.UUID;

import dev.veecodekub.paymentservice.enumerate.PaymentMethod;

public record OrderStockReservedEvent(
        UUID orderId,
        Long customerId,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        String paymentCurrency) {

}
