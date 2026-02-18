package dev.veecodekub.authservice.props;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String issuer,
        Duration expiration,
        RSAPublicKey publicKey,
        RSAPrivateKey privateKey) {
}