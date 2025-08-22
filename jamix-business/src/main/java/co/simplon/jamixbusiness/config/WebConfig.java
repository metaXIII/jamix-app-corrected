package co.simplon.jamixbusiness.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  private final JamixConfig jamixConfig;
  private static final String PATH_PORTFOLIOS_OWNED = "/portfolios/owned";
  private static final String PATH_OFFERS_MATCHS = "/offers/**";
  private static final String PATH_IMAGES = "/images/**";

  public WebConfig(final JamixConfig jamixConfig) {
    this.jamixConfig = jamixConfig;
  }

  @Bean
  @Profile("!prod")
  SecurityFilterChain devFilterChain(HttpSecurity http) throws Exception {
    return http
      .cors(Customizer.withDefaults())
      .csrf(AbstractHttpConfigurer::disable)
      .authorizeHttpRequests(authorize ->
        authorize
          .requestMatchers(HttpMethod.POST, "/account/signup", "/account/login")
          .anonymous()
          .requestMatchers(HttpMethod.GET, "/offers/owned", PATH_PORTFOLIOS_OWNED)
          .authenticated()
          .requestMatchers(HttpMethod.GET, PATH_OFFERS_MATCHS, "/api/**", PATH_IMAGES, "/portfolios/**")
          .permitAll()
          .requestMatchers(HttpMethod.POST, "/offers/contact/**")
          .permitAll()
          .requestMatchers(HttpMethod.POST, "/offers", PATH_PORTFOLIOS_OWNED, PATH_IMAGES)
          .authenticated()
          .requestMatchers(HttpMethod.PATCH, PATH_OFFERS_MATCHS, PATH_IMAGES, PATH_PORTFOLIOS_OWNED)
          .authenticated()
          .requestMatchers(HttpMethod.DELETE, PATH_OFFERS_MATCHS, PATH_IMAGES, PATH_PORTFOLIOS_OWNED)
          .authenticated()
          .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
          .anonymous()
      )
      .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
      .build();
  }

  @Bean
  @Profile("prod")
  SecurityFilterChain prodFilterChain(HttpSecurity http) throws Exception {
    return http
      .csrf(AbstractHttpConfigurer::disable)
      .authorizeHttpRequests(authorize ->
        authorize
          .requestMatchers(HttpMethod.POST, "/account/signup", "/account/login")
          .anonymous()
          .requestMatchers(HttpMethod.GET, "/offers/owned", PATH_PORTFOLIOS_OWNED)
          .authenticated()
          .requestMatchers(HttpMethod.GET, PATH_OFFERS_MATCHS, "/api/**", PATH_IMAGES, "/portfolios/**")
          .permitAll()
          .requestMatchers(HttpMethod.POST, "/offers", PATH_IMAGES, PATH_PORTFOLIOS_OWNED)
          .authenticated()
          .requestMatchers(HttpMethod.PATCH, PATH_OFFERS_MATCHS, PATH_IMAGES, PATH_PORTFOLIOS_OWNED)
          .authenticated()
          .requestMatchers(HttpMethod.DELETE, PATH_OFFERS_MATCHS, PATH_IMAGES, PATH_PORTFOLIOS_OWNED)
          .authenticated()
      )
      .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
      .build();
  }

  @Bean
  @Profile("!prod")
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of(jamixConfig.urlConfig().cors()));
    config.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE"));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
