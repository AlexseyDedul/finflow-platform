package com.dedul.finflow.app.finflowapp.shared.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

class CurrentUserProviderTest {
  private final CurrentUserProvider provider = new CurrentUserProvider();

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void shouldExtractCurrentUserFromJwt() {
    UUID employeeId = UUID.randomUUID();

    Jwt jwt =
        Jwt.withTokenValue("token")
            .header("alg", "none")
            .claim("sub", "subject-1")
            .claim("preferred_username", "employee")
            .claim("employeeId", employeeId.toString())
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(300))
            .build();

    JwtAuthenticationToken authentication =
        new JwtAuthenticationToken(
            jwt,
            List.of(
                new SimpleGrantedAuthority("ROLE_EMPLOYEE"),
                new SimpleGrantedAuthority("SCOPE_profile")));

    SecurityContextHolder.getContext().setAuthentication(authentication);

    CurrentUser currentUser = provider.getCurrentUser();

    assertThat(currentUser.subject()).isEqualTo("subject-1");
    assertThat(currentUser.username()).isEqualTo("employee");
    assertThat(currentUser.employeeId()).isEqualTo(employeeId);
    assertThat(currentUser.roles()).containsExactly("EMPLOYEE");
    assertThat(currentUser.hasRole("EMPLOYEE")).isTrue();
  }

  @Test
  void shouldRejectMissingEmployeeIdClaim() {
    Jwt jwt =
        Jwt.withTokenValue("token")
            .header("alg", "none")
            .claim("sub", "subject-1")
            .claim("preferred_username", "employee")
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(300))
            .build();

    SecurityContextHolder.getContext()
        .setAuthentication(new JwtAuthenticationToken(jwt, List.of()));

    assertThatThrownBy(provider::getCurrentUser)
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("JWT does not contain employeeId claim");
  }
}
