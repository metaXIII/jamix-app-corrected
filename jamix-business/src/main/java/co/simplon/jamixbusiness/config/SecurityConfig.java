package co.simplon.jamixbusiness.config;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import com.auth0.jwt.algorithms.Algorithm;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@Configuration
@OpenAPIDefinition(security = @SecurityRequirement(name = "bearerAuth"))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class SecurityConfig {

    private final JamixConfiguration jamixConfiguration;

    public SecurityConfig(JamixConfiguration jamixConfiguration) {
        this.jamixConfiguration = jamixConfiguration;
    }

    @Bean
    PasswordEncoder encoder() {
        return new BCryptPasswordEncoder(jamixConfiguration.bcryptConfiguration().rounds());
    }

    @Bean
    JwtProvider jwtProvider() {
        Algorithm algorithm = Algorithm.HMAC256(jamixConfiguration.jwtConfiguration().secret());
        return new JwtProvider(algorithm, jamixConfiguration.jwtConfiguration().exp(), jamixConfiguration.jwtConfiguration().issuer());
    }

    @Bean
    JwtDecoder jwtDecoder() {
        SecretKey secretKey = new SecretKeySpec(jamixConfiguration.jwtConfiguration().secret().getBytes(), "HmacSHA256");
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(secretKey).macAlgorithm(MacAlgorithm.HS256).build();
        OAuth2TokenValidator<Jwt> validator = JwtValidators.createDefaultWithIssuer(jamixConfiguration.jwtConfiguration().issuer());
        decoder.setJwtValidator(validator);
        return decoder;
    }
}
