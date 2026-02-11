package dev.veecodekub.productservice.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ProductCreationRequest(
        @NotNull(message = "Name is required") @NotBlank(message = "Name is required") String name,
        @NotNull(message = "Description is required") @NotBlank(message = "Description is required") String description,
        @NotNull(message = "Price is required") @PositiveOrZero(message = "Price must be positive or zero") BigDecimal price,
        @NotNull(message = "Quantity is required") @PositiveOrZero(message = "Quantity must be positive or zero") Integer quantity) {

}
