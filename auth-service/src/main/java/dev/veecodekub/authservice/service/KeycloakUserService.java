package dev.veecodekub.authservice.service;

import java.util.Collections;
import java.util.List;

import org.jspecify.annotations.NonNull;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import dev.veecodekub.authservice.dto.UserDto;
import dev.veecodekub.authservice.props.KeycloakProperties;
import dev.veecodekub.common.error.BusinessException;
import dev.veecodekub.common.error.auth.AuthErrorCode;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakUserService {

    private final Keycloak keycloakAdminClient;
    private final KeycloakProperties props;

    public String createUser(UserDto userDto) {
        UsersResource resource = usersResource();
        UserRepresentation representation = toUserRepresentation(userDto);

        try (Response response = resource.create(representation)) {
            if (response.getStatus() == 201) {
                String userId = response.getLocation().getPath().split("/")[5];
                log.info("User created successfully: {}", userId);
                UserResource user = userResource(userId);
                log.info("User: {}", user.credentials());
                return userId;
            } else if (response.getStatus() == 409) {
                throw new BusinessException(AuthErrorCode.USERNAME_ALREADY_EXISTS);
            }
        } catch (Exception e) {
            log.error("Failed to create user: {}", e.getMessage());
        }
        return "Failed to create user";
    }

    public void updateUser(String userId, UserDto userDto) {
        UserResource user = userResource(userId);
        UserRepresentation representation = toUserRepresentation(userDto);
        user.update(representation);
    }

    public void deleteUser(String userId) {
        UserResource user = userResource(userId);
        user.remove();
    }

    public void sendVerificationLink(String userId) {
        UserResource user = userResource(userId);

        if (Boolean.TRUE.equals(user.toRepresentation().isEmailVerified())) {
            throw new BusinessException(AuthErrorCode.EMAIL_ALREADY_VERIFIED);
        }

        user.sendVerifyEmail();
    }

    public void sendResetPassword(String userId) {
        UserResource user = userResource(userId);
        user.executeActionsEmail(List.of("UPDATE_PASSWORD"));
    }

    private UsersResource usersResource() {
        return keycloakAdminClient.realm(props.adminRealm()).users();
    }

    private UserResource userResource(String userId) {
        UserResource user = usersResource().get(userId);

        if (user == null) {
            throw new BusinessException(AuthErrorCode.INVALID_CREDENTIALS);
        }

        return user;
    }

    private @NonNull UserRepresentation toUserRepresentation(UserDto userDto) {
        var user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(userDto.username());
        user.setEmail(userDto.email());
        user.setFirstName(userDto.firstName());
        user.setLastName(userDto.lastName());

        var credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(userDto.password());
        credential.setTemporary(false);

        user.setCredentials(Collections.singletonList(credential));

        return user;
    }
}
