package dev.veecodekub.common.error.order;

import org.springframework.http.HttpStatus;

import dev.veecodekub.common.error.ErrorCategory;
import dev.veecodekub.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderErrorCode implements ErrorCode {

    ORDER_NOT_FOUND(
            "ORD-NOT_FOUND-001",
            "Order not found",
            ErrorCategory.NOT_FOUND,
            HttpStatus.NOT_FOUND,
            false),

    INVALID_ORDER_STATE(
            "ORD-STATE-001",
            "Invalid order state",
            ErrorCategory.STATE,
            HttpStatus.CONFLICT,
            false);

    private final String code;
    private final String message;
    private final ErrorCategory category;
    private final HttpStatus httpStatus;
    private final boolean retryable;
}
