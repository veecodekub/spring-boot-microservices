package dev.veecodekub.userservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;

import dev.veecodekub.userservice.dto.ProfileRequest;
import dev.veecodekub.userservice.dto.ProfileResponse;
import dev.veecodekub.userservice.model.Profile;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProfileMapper {

    @Mapping(target = "id", ignore = true)
    Profile toProfile(ProfileRequest request, Long userId);

    ProfileResponse toProfileResponse(Profile profile);
}
