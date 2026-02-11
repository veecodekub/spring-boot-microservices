package dev.veecodekub.orderservice.enumerate;

public enum OrderStatus {
    CREATED,
    INVENTORY_RESERVED,
    PAYMENT_PENDING,
    PAID,
    CANCELLED,
    REFUNDED
}
