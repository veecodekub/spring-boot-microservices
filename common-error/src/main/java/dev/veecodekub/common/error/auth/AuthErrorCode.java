package dev.veecodekub.common.error.auth;

import org.springframework.http.HttpStatus;

import dev.veecodekub.common.error.ErrorCategory;
import dev.veecodekub.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    USERNAME_ALREADY_EXISTS(
            "USERNAME_ALREADY_EXISTS",
            "Username already exists",
            ErrorCategory.AUTH,
            HttpStatus.CONFLICT,
            false),

    INVALID_CREDENTIALS(
            "INVALID_CREDENTIALS",
            "Invalid credentials",
            ErrorCategory.AUTH,
            HttpStatus.UNAUTHORIZED,
            false);

    private final String code;
    private final String message;
    private final ErrorCategory category;
    private final HttpStatus httpStatus;
    private final boolean retryable;
}
