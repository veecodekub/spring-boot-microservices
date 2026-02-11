package dev.veecodekub.common.error.payment;

import org.springframework.http.HttpStatus;

import dev.veecodekub.common.error.ErrorCategory;
import dev.veecodekub.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentErrorCode implements ErrorCode {

    PAYMENT_FAILED(
            "PAY-PROCESS-001",
            "Payment failed",
            ErrorCategory.PAYMENT,
            HttpStatus.BAD_REQUEST,
            false),

    PAYMENT_TIMEOUT(
            "PAY-PROCESS-002",
            "Payment timeout",
            ErrorCategory.PAYMENT,
            HttpStatus.GATEWAY_TIMEOUT,
            true),

    PAYMENT_NOT_FOUND(
            "PAY-PROCESS-003",
            "Payment not found",
            ErrorCategory.PAYMENT,
            HttpStatus.NOT_FOUND,
            false),

    PAYMENT_NOT_PENDING(
            "PAY-PROCESS-004",
            "Payment is not in pending state",
            ErrorCategory.PAYMENT,
            HttpStatus.BAD_REQUEST,
            false),

    PAYMENT_ALREADY_EXISTS(
            "PAY-PROCESS-005",
            "Payment already exists",
            ErrorCategory.PAYMENT,
            HttpStatus.BAD_REQUEST,
            false);

    private final String code;
    private final String message;
    private final ErrorCategory category;
    private final HttpStatus httpStatus;
    private final boolean retryable;
}
