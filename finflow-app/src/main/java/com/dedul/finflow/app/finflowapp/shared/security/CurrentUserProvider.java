package com.dedul.finflow.app.finflowapp.shared.security;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserProvider {
  public CurrentUser getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
      throw new IllegalStateException("No authenticated JWT user found");
    }

    String employeeId = jwt.getClaimAsString("employeeId");

    if (employeeId == null || employeeId.isBlank()) {
      throw new IllegalStateException("JWT does not contain employeeId claim");
    }

    Set<String> roles =
        authentication.getAuthorities().stream()
            .map(authority -> authority.getAuthority())
            .filter(authority -> authority.startsWith("ROLE_"))
            .map(authority -> authority.substring("ROLE_".length()))
            .collect(Collectors.toSet());

    return new CurrentUser(
        jwt.getSubject(),
        jwt.getClaimAsString("preferred_username"),
        UUID.fromString(employeeId),
        roles);
  }
}
