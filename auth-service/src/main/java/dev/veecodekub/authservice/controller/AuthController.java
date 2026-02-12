package dev.veecodekub.authservice.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.veecodekub.authservice.dto.LoginRequest;
import dev.veecodekub.authservice.dto.TokenResponse;
import dev.veecodekub.authservice.dto.UserDto;
import dev.veecodekub.authservice.service.AuthService;
import dev.veecodekub.authservice.service.KeycloakUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final KeycloakUserService keycloakUserService;

    @PostMapping("/register")
    public void register(@Valid @RequestBody UserDto request) {
        keycloakUserService.createUser(request);
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
