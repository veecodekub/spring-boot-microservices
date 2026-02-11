package dev.veecodekub.inventoryservice.dto;

import java.math.BigDecimal;

public record OrderItemSnapshot(
        Long productId,
        String productName,
        BigDecimal productPrice,
        Integer quantity,
        BigDecimal total) {
}