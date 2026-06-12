package com.dedul.finflow.app.finflowapp.expense.api.dto;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ExpenseControllerValidationTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private RuntimeService runtimeService;

  @MockitoBean private TaskService taskService;

  @MockitoBean private JwtDecoder jwtDecoder;

  @Test
  void shouldReturnValidationProblemWhenCreateExpenseRequestIsInvalid() throws Exception {
    mockMvc
        .perform(
            post("/api/expenses")
                .with(
                    jwt()
                        .jwt(jwt -> jwt.claim("employeeId", "00000000-0000-0000-0000-000000000001"))
                        .authorities(new SimpleGrantedAuthority("ROLE_EMPLOYEE")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                                                    {
                                                      "amount": 0,
                                                      "currency": "",
                                                      "category": null,
                                                      "description": ""
                                                    }
                                                    """))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(jsonPath("$.type").value("https://finflow.local/problems/validation"))
        .andExpect(jsonPath("$.title").value("Validation failed"))
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.instance").value("/api/expenses"));
  }
}
