package dev.veecodekub.orderservice.dto;

import java.util.UUID;

public record InventoryRequest(
        UUID orderId) {
}
