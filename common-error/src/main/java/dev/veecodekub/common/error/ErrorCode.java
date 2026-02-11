package dev.veecodekub.common.error;

import org.springframework.http.HttpStatus;

public interface ErrorCode {

    String getCode();

    String getMessage();

    ErrorCategory getCategory();

    HttpStatus getHttpStatus();

    boolean isRetryable();
}
