package dev.veecodekub.authservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import dev.veecodekub.authservice.props.JwtProperties;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class JwtConfig {

    private final JwtProperties props;

    @Bean
    JWKSource<SecurityContext> jwkSource() {
        JWK jwk = new RSAKey.Builder(props.publicKey())
                .privateKey(props.privateKey())
                .keyID(props.issuer())
                .build();

        return new ImmutableJWKSet<>(new JWKSet(jwk));
    }

    @Bean
    JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(props.publicKey()).build();
    }
}
