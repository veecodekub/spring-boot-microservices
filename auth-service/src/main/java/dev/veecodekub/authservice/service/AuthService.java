package dev.veecodekub.authservice.service;

import java.util.Collections;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.veecodekub.authservice.dto.LoginRequest;
import dev.veecodekub.authservice.dto.RegisterRequest;
import dev.veecodekub.authservice.dto.TokenResponse;
import dev.veecodekub.authservice.model.AuthUser;
import dev.veecodekub.authservice.repository.AuthUserRepository;
import dev.veecodekub.common.error.BusinessException;
import dev.veecodekub.common.error.auth.AuthErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final KafkaTemplate<String, Long> kafkaTemplate;

    @Transactional
    public TokenResponse register(RegisterRequest request) {
        log.debug("register-[next]:(registering user). username: {}", request.username());

        if (userRepository.existsByUsername(request.username())) {
            log.warn("register-[block]:(username already exists). username: {}", request.username());
            throw new BusinessException(AuthErrorCode.USERNAME_ALREADY_EXISTS);
        }

        AuthUser authUser = userRepository.save(AuthUser.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .roles(Collections.singleton(request.role()))
                .build());

        kafkaTemplate.send("user-created", authUser.getId());
        log.debug("register-[next]:(sent user created event). userId: {}", authUser.getId());

        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        String token = tokenService.generateToken(authenticate);
        TokenResponse response = new TokenResponse(authUser.getId(), token);
        log.debug("register-[next]:(user registered successfully). response: {}", response);

        return response;
    }

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        log.debug("login-[next]:(logging in user). request: {}", request);

        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        String token = tokenService.generateToken(authenticate);
        TokenResponse response = new TokenResponse(0L, token);

        log.debug("login-[next]:(user logged in successfully). response: {}", response);

        return response;
    }
}
