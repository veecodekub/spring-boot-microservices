package dev.veecodekub.common.error;

import java.time.Instant;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {

    private String code;
    private String message;
    private Map<String, String> details;
    private ErrorCategory category;
    private boolean retryable;
    private Instant timestamp;

    public static ErrorResponse from(ErrorCode errorCode) {
        return ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .details(null)
                .category(errorCode.getCategory())
                .retryable(errorCode.isRetryable())
                .timestamp(Instant.now())
                .build();
    }

    public static ErrorResponse validation(Map<String, String> details) {
        return ErrorResponse.builder()
                .code("COMMON-400-001")
                .message("Invalid request")
                .details(details)
                .category(ErrorCategory.VALIDATION)
                .retryable(false)
                .timestamp(Instant.now())
                .build();
    }

    public static ErrorResponse system() {
        return new ErrorResponse(
                "SYS-500-001",
                "Internal server error",
                null,
                ErrorCategory.SYSTEM,
                false,
                Instant.now());
    }
}
