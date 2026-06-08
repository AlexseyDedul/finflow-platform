package com.dedul.finflow.app.finflowapp.workflow.application;

import com.dedul.finflow.app.finflowapp.workflow.api.dto.WorkflowTaskResponse;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.TaskService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkflowTaskService {
  private final TaskService taskService;

  public List<WorkflowTaskResponse> getOpenTasks() {
    return taskService.createTaskQuery().active().list().stream()
        .map(
            task -> {
              Object expenseIdValue = taskService.getVariable(task.getId(), "expenseId");

              UUID expenseId = expenseIdValue == null
                  ? null
                  : UUID.fromString(expenseIdValue.toString());
              return new WorkflowTaskResponse(
                  task.getId(),
                  task.getName(),
                  task.getProcessInstanceId(),
                  expenseId);
            })
        .toList();
  }

  public void approve(String taskId) {
    taskService.complete(taskId, Map.of("approved", true));
  }

  public void reject(String taskId, String reason) {
    taskService.complete(taskId, Map.of("approved", false, "rejectionReason", reason));
  }
}
