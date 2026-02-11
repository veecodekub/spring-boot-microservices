package dev.veecodekub.userservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.veecodekub.userservice.model.Profile;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    boolean existsByUserId(Long userId);

    Optional<Profile> findByUserId(Long userId);
}
