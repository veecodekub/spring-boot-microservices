package dev.veecodekub.common.error.product;

import org.springframework.http.HttpStatus;

import dev.veecodekub.common.error.ErrorCategory;
import dev.veecodekub.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements ErrorCode {

    PRODUCT_NOT_FOUND(
            "PRODUCT_NOT_FOUND",
            "Product not found",
            ErrorCategory.NOT_FOUND,
            HttpStatus.NOT_FOUND,
            false);

    private final String code;
    private final String message;
    private final ErrorCategory category;
    private final HttpStatus httpStatus;
    private final boolean retryable;
}
