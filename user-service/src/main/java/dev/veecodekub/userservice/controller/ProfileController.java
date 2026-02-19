package dev.veecodekub.userservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.veecodekub.userservice.annotation.CurrentUserId;
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
    public ProfileResponse getProfile(@CurrentUserId Long userId) {
        return profileService.getProfile(userId);
    }

    @PutMapping
    public ProfileResponse updateProfile(
            @CurrentUserId Long userId,
            @RequestBody ProfileRequest request) {
        return profileService.updateProfile(userId, request);
    }
}
