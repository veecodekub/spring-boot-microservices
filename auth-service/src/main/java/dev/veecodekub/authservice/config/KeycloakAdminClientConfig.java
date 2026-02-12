package dev.veecodekub.authservice.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.veecodekub.authservice.props.KeycloakProperties;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class KeycloakAdminClientConfig {

    private final KeycloakProperties props;

    @Bean
    Keycloak keycloakAdminClient() {
        return KeycloakBuilder.builder()
                .serverUrl(props.authServerUrl())
                .realm(props.adminRealm())
                .clientId(props.adminClientId())
                .username(props.adminUsername())
                .password(props.adminPassword())
                .grantType(OAuth2Constants.PASSWORD)
                .build();
    }
}
