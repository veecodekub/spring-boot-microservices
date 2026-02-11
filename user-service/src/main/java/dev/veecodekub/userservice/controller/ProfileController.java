package dev.veecodekub.userservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.veecodekub.userservice.dto.ProfileRequest;
import dev.veecodekub.userservice.dto.ProfileResponse;
import dev.veecodekub.userservice.service.ProfileService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public ProfileResponse getProfile(@RequestHeader("X-User-Id") Long userId) {
        return profileService.getProfile(userId);
    }

    @PostMapping
    public ProfileResponse createProfile(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody ProfileRequest request) {
        return profileService.createProfile(userId, request);
    }

    @PutMapping
    public ProfileResponse updateProfile(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody ProfileRequest request) {
        return profileService.updateProfile(userId, request);
    }
}
