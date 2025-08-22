package co.simplon.jamixbusiness.config;

import com.auth0.jwt.algorithms.Algorithm;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

@Component
@OpenAPIDefinition(security = @SecurityRequirement(name = "bearerAuth"))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public record JamixConfig(
  JamixBcryptConfig bcryptConfig,
  JamixJwtConfig jwtConfig,
  EmailConfig emailConfig,
  ApiConfig apiConfig,
  UrlConfig urlConfig,
  ScwConfig scwConfig
) {
  @ConfigurationProperties(value = "jamix.bcrypt")
  public record JamixBcryptConfig(int rounds) {}

  @ConfigurationProperties(value = "jamix.jwt")
  public record JamixJwtConfig(String secret, Long exp, String issuer) {}

  @ConfigurationProperties(value = "jamix.email")
  public record EmailConfig(String from) {}

  @ConfigurationProperties(value = "jamix.api")
  public record ApiConfig(String geo) {}

  @ConfigurationProperties(value = "jamix.url")
  public record UrlConfig(String images, String cors) {}

  @ConfigurationProperties(value = "scw")
  public record ScwConfig(String accessKey, String secretKey, String region, String bucket) {}

  @Bean
  PasswordEncoder encoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  JwtProvider jwtProvider() {
    Algorithm algorithm = Algorithm.HMAC256(jwtConfig().secret());
    return new JwtProvider(algorithm, jwtConfig().exp(), jwtConfig().issuer());
  }

  @Bean
  JwtDecoder jwtDecoder() {
    SecretKey secretKey = new SecretKeySpec(jwtConfig().secret().getBytes(), "HmacSHA256");
    NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(secretKey).macAlgorithm(MacAlgorithm.HS256).build();
    OAuth2TokenValidator<Jwt> validator = JwtValidators.createDefaultWithIssuer(jwtConfig().issuer());
    decoder.setJwtValidator(validator);
    return decoder;
  }
}
