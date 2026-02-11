package dev.veecodekub.inventoryservice.dto;

import java.math.BigDecimal;
import java.util.UUID;

import dev.veecodekub.inventoryservice.enumerate.PaymentMethod;

public record OrderStockReservedEvent(
        UUID orderId,
        Long customerId,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        String paymentCurrency) {

}
