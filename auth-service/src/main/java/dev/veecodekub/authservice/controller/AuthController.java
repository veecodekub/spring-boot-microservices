package dev.veecodekub.authservice.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;

import dev.veecodekub.authservice.dto.LoginRequest;
import dev.veecodekub.authservice.dto.RegisterRequest;
import dev.veecodekub.authservice.dto.TokenResponse;
import dev.veecodekub.authservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RSAKey rsaKey;
    private final AuthService authService;

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> getJwkSetKeys() {
        return new JWKSet(rsaKey.toPublicJWK()).toJSONObject();
    }

    @PostMapping("/register")
    public TokenResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
