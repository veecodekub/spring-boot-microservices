package dev.veecodekub.productservice.dto;

public record CreateInventoryRequest(
        Long productId,
        Integer quantity) {

}
