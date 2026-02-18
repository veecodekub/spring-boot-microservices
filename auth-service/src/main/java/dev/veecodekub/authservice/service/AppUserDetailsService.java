package dev.veecodekub.authservice.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import dev.veecodekub.authservice.model.AppUser;
import dev.veecodekub.authservice.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppUserDetailsService implements UserDetailsService {

    private final AppUserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("loadUserByUsername -> username: {}", username);

        AppUser user = repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        return User.builder()
                .username(username)
                .password(user.getPassword())
                .roles("USER", "ADMIN")
                .disabled(false)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .build();
    }

}
