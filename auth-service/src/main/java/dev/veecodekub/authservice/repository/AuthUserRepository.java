package dev.veecodekub.authservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.veecodekub.authservice.model.AuthUser;

@Repository
public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {
    boolean existsByUsername(String username);

    Optional<AuthUser> findByUsername(String username);
}
