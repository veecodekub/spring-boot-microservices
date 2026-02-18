package dev.veecodekub.authservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.veecodekub.authservice.model.AppUser;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    boolean existsByUsername(String username);

    Optional<AppUser> findByUsername(String username);
}
