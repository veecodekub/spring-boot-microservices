package dev.veecodekub.userservice.dto;

public record ProfileResponse(
        Long id,
        String name,
        String phone,
        String address) {

}
