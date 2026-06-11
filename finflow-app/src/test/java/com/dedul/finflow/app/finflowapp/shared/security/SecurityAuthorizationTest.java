package com.dedul.finflow.app.finflowapp.shared.security;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.TaskQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityAuthorizationTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private RuntimeService runtimeService;

  @MockitoBean private TaskService taskService;

  @MockitoBean private JwtDecoder jwtDecoder;

  @MockitoBean private TaskQuery taskQuery;

  @BeforeEach
  void setUp() {
    when(taskService.createTaskQuery()).thenReturn(taskQuery);
    when(taskQuery.active()).thenReturn(taskQuery);
    when(taskQuery.list()).thenReturn(List.of());
  }

  @Test
  void workflowTasks_shouldRequireAuthentication() throws Exception {
    mockMvc.perform(get("/api/workflow/tasks")).andExpect(status().isUnauthorized());
  }

  @Test
  void workflowTasks_shouldRejectEmployeeRole() throws Exception {
    mockMvc
        .perform(
            get("/api/workflow/tasks")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))))
        .andExpect(status().isForbidden());
  }

  @Test
  void workflowTasks_shouldAllowManagerRole() throws Exception {
    mockMvc
        .perform(
            get("/api/workflow/tasks")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_MANAGER"))))
        .andExpect(status().isOk());
  }

  @Test
  void ledger_shouldRejectEmployeeRole() throws Exception {
    mockMvc
        .perform(
            get("/api/ledger/accounts")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))))
        .andExpect(status().isForbidden());
  }

  @Test
  void ledger_shouldAllowFinanceRolePastSecurityLayer() throws Exception {
    mockMvc
        .perform(
            get("/api/ledger/accounts")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_FINANCE"))))
        .andExpect(status().isNotFound());
  }
}
