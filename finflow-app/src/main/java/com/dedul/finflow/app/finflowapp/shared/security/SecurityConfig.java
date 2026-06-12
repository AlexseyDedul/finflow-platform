package com.dedul.finflow.app.finflowapp.shared.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(
                        "/actuator/health",
                        "/actuator/health/**",
                        "/actuator/info",
                        "/actuator/prometheus",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**")
                    .permitAll()
                    .requestMatchers("/api/workflow/tasks/*/approve")
                    .hasAnyRole("MANAGER", "FINANCE", "ADMIN")
                    .requestMatchers("/api/workflow/tasks/*/reject")
                    .hasAnyRole("MANAGER", "FINANCE", "ADMIN")
                    .requestMatchers("/api/workflow/tasks")
                    .hasAnyRole("MANAGER", "FINANCE", "ADMIN")
                    .requestMatchers("/api/reports/**")
                    .hasAnyRole("FINANCE", "ADMIN")
                    .requestMatchers("/api/ledger/**")
                    .hasAnyRole("FINANCE", "ADMIN")
                    .requestMatchers("/api/expenses/**")
                    .hasAnyRole("EMPLOYEE", "MANAGER", "FINANCE", "ADMIN")
                    .requestMatchers("/api/documents/**")
                    .hasAnyRole("EMPLOYEE", "MANAGER", "FINANCE", "ADMIN")
                    .anyRequest()
                    .authenticated())
        .oauth2ResourceServer(
            oauth2 ->
                oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
        .build();
  }

  @Bean
  Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
    JwtGrantedAuthoritiesConverter scopesConverter = new JwtGrantedAuthoritiesConverter();

    return jwt -> {
      Collection<GrantedAuthority> authorities = new ArrayList<>(scopesConverter.convert(jwt));
      Map<String, Object> realmAccess = jwt.getClaim("realm_access");
      if (realmAccess != null && realmAccess.get("roles") instanceof Collection<?> roles) {
        authorities.addAll(
            roles.stream()
                .map(Object::toString)
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet()));
      }
      return new JwtAuthenticationToken(jwt, authorities, jwt.getSubject());
    };
  }
}
