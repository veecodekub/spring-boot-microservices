package dev.veecodekub.userservice.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import dev.veecodekub.userservice.model.Profile;
import dev.veecodekub.userservice.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {

    private final ProfileRepository profileRepository;

    @KafkaListener(topics = "user-created", groupId = "user-service")
    public void handleUserCreated(Long userId) {
        log.debug("handleUserCreated-[next]:(user created). event: {}", userId);
        profileRepository.save(Profile.builder()
                .userId(userId)
                .build());
        log.debug("handleUserCreated-[next]:(user created successfully) event: {}", userId);
    }
}