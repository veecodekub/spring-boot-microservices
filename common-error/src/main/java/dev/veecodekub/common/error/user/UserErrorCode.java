package dev.veecodekub.common.error.user;

import org.springframework.http.HttpStatus;

import dev.veecodekub.common.error.ErrorCategory;
import dev.veecodekub.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    PROFILE_ALREADY_EXISTS(
            "PROFILE_ALREADY_EXISTS",
            "Profile already exists",
            ErrorCategory.VALIDATION,
            HttpStatus.CONFLICT,
            false),

    PROFILE_NOT_FOUND(
            "PROFILE_NOT_FOUND",
            "Profile not found",
            ErrorCategory.NOT_FOUND,
            HttpStatus.NOT_FOUND,
            false);

    private final String code;
    private final String message;
    private final ErrorCategory category;
    private final HttpStatus httpStatus;
    private final boolean retryable;
}
