package dev.veecodekub.authservice.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.veecodekub.authservice.dto.LoginRequest;
import dev.veecodekub.authservice.dto.RegisterRequest;
import dev.veecodekub.authservice.dto.TokenResponse;
import dev.veecodekub.authservice.model.AppUser;
import dev.veecodekub.authservice.repository.AppUserRepository;
import dev.veecodekub.common.error.BusinessException;
import dev.veecodekub.common.error.auth.AuthErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @Transactional
    public TokenResponse register(RegisterRequest request) {
        log.debug("register-[next]:(registering user). request: {}", request);

        if (userRepository.existsByUsername(request.username())) {
            log.warn("register-[block]:(username already exists). request: {}", request);
            throw new BusinessException(AuthErrorCode.USERNAME_ALREADY_EXISTS);
        }

        AppUser user = AppUser.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .build();
        userRepository.save(user);
        // String token = jwtService.generateToken(user.getUsername());
        // log.debug("register-[next]:(user registered successfully). response: {}",
        // new TokenResponse(user.getId(), token));

        return new TokenResponse(0L, "token");
    }

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        log.debug("login-[next]:(logging in user). request: {}", request);

        // User user = userRepository.findByUsername(request.username())
        // .orElseThrow(() -> {
        // log.warn("login-[block]:(user not found). request: {}", request);
        // return new BusinessException(AuthErrorCode.INVALID_CREDENTIALS);
        // });

        // if (!passwordEncoder.matches(request.password(), user.getPassword())) {
        // log.warn("login-[block]:(invalid credentials). request: {}", request);
        // throw new BusinessException(AuthErrorCode.INVALID_CREDENTIALS);
        // }

        // String token = jwtService.generateToken(user.getUsername());
        // log.debug("login-[next]:(user logged in successfully). response: {}",
        // new TokenResponse(user.getId(), token));

        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        String token = tokenService.generateToken(authenticate);

        return new TokenResponse(0L, token);
    }
}
