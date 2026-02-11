package dev.veecodekub.common.error.inventory;

import org.springframework.http.HttpStatus;

import dev.veecodekub.common.error.ErrorCategory;
import dev.veecodekub.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InventoryErrorCode implements ErrorCode {

    PRODUCT_NOT_FOUND(
            "INV-NOT_FOUND-001",
            "Product not found",
            ErrorCategory.NOT_FOUND,
            HttpStatus.NOT_FOUND,
            false),

    OUT_OF_STOCK(
            "INV-STOCK-001",
            "Out of stock",
            ErrorCategory.STOCK,
            HttpStatus.CONFLICT,
            false),

    PRODUCT_ALREADY_EXISTS(
            "INV-ALREADY_EXISTS-001",
            "Product already exists",
            ErrorCategory.STOCK,
            HttpStatus.CONFLICT,
            false);

    private final String code;
    private final String message;
    private final ErrorCategory category;
    private final HttpStatus httpStatus;
    private final boolean retryable;

}