package dev.veecodekub.paymentservice.enumerate;

public enum OrderStatus {
    CREATED,
    INVENTORY_RESERVED,
    PAYMENT_PENDING,
    PAID,
    CANCELLED
}
