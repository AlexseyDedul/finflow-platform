package com.dedul.finflow.app.finflowapp;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class FinflowApplicationTest {

  @MockitoBean
  private RuntimeService runtimeService;

  @MockitoBean
  private TaskService taskService;

  @Test
  void contextLoads() {}
}
