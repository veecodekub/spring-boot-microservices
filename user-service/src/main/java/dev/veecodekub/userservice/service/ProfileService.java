package dev.veecodekub.userservice.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.veecodekub.common.error.BusinessException;
import dev.veecodekub.common.error.user.UserErrorCode;
import dev.veecodekub.userservice.dto.ProfileRequest;
import dev.veecodekub.userservice.dto.ProfileResponse;
import dev.veecodekub.userservice.mapper.ProfileMapper;
import dev.veecodekub.userservice.model.Profile;
import dev.veecodekub.userservice.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final ProfileMapper profileMapper;

    @Transactional(readOnly = true)
    public ProfileResponse getProfile(Long userId) {
        log.debug("getProfile-[next]:(getting profile). userId: {}", userId);

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("getProfile-[warn]:(profile not found). userId: {}", userId);
                    return new BusinessException(UserErrorCode.PROFILE_NOT_FOUND);
                });

        ProfileResponse response = profileMapper.toProfileResponse(profile);
        log.debug("getProfile-[next]:(profile found). response: {}", response);

        return response;
    }

    @Transactional
    public ProfileResponse updateProfile(Long userId, ProfileRequest request) {
        log.debug("updateProfile-[next]:(updating profile). userId: {}, request: {}", userId, request);

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("updateProfile-[warn]:(profile not found). userId: {}", userId);
                    return new BusinessException(UserErrorCode.PROFILE_NOT_FOUND);
                });

        profile.setName(request.name());
        profile.setPhone(request.phone());
        profile.setAddress(request.address());
        profileRepository.save(profile);

        ProfileResponse response = profileMapper.toProfileResponse(profile);
        log.debug("updateProfile-[next]:(profile updated). response: {}", response);

        return response;
    }
}
