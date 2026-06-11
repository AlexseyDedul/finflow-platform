package com.dedul.finflow.app.finflowapp.shared.security;

import java.util.Set;
import java.util.UUID;

public record CurrentUser(String subject, String username, UUID employeeId, Set<String> roles) {
  public boolean hasRole(String role) {
    return roles.contains(role);
  }
}
