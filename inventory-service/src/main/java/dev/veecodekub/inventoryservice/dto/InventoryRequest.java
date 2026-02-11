package dev.veecodekub.inventoryservice.dto;

public record InventoryRequest(
        Long productId,
        Integer quantity) {

}
