package dev.veecodekub.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "Username is required") @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters") @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Username must contain only letters and numbers") String username,
        @NotBlank(message = "Password is required") @Size(min = 8, max = 16, message = "Password must be between 8 and 16 characters") String password) {
}
