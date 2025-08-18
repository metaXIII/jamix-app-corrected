package co.simplon.jamixbusiness.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
public record JamixConfiguration(
        BCryptConfiguration bcryptConfiguration,
        JwtConfiguration jwtConfiguration
) {
    @ConfigurationProperties(prefix = "jamix.bcrypt")
    public record BCryptConfiguration(Integer rounds) {
    }

    @ConfigurationProperties(prefix = "jamix.jwt")
    public record JwtConfiguration(String secret, Long exp, String issuer) {
    }
}
