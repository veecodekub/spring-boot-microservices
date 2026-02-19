package dev.veecodekub.authservice.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.veecodekub.authservice.dto.AuthUserDetails;
import dev.veecodekub.authservice.model.AuthUser;
import dev.veecodekub.authservice.repository.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthUserDetailsService implements UserDetailsService {

    private final AuthUserRepository repository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("loadUserByUsername -> username: {}", username);

        AuthUser authUser = repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid Username"));

        Set<String> roles = Optional.ofNullable(authUser.getRoles()).orElse(Collections.emptySet());

        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        return new AuthUserDetails(
                authUser.getId(),
                authUser.getUsername(),
                authUser.getPassword(),
                authorities);
    }

}
