package dev.veecodekub.authservice.dto;

public record UserDto(
        String username,
        String email,
        String password,
        String firstName,
        String lastName) {
}