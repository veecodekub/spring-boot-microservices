package dev.veecodekub.authservice.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "keycloak")
public record KeycloakProperties(
        String authServerUrl,
        String adminRealm,
        String adminClientId,
        String adminUsername,
        String adminPassword) {

}
