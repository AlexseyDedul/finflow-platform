package com.dedul.finflow.app.finflowapp.workflow.application;

import com.dedul.finflow.app.finflowapp.workflow.api.dto.WorkflowTaskResponse;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.TaskService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WorkflowTaskService {
  private final TaskService taskService;

  public List<WorkflowTaskResponse> getOpenTasks() {
    return taskService.createTaskQuery()
        .active()
        .list()
        .stream()
        .map(task -> new WorkflowTaskResponse(
            task.getId(),
            task.getName(),
            task.getProcessInstanceId()
        ))
        .toList();
  }

  public void approve(String taskId) {
    taskService.complete(taskId, Map.of("approved", true));
  }

  public void reject(String taskId, String reason) {
    taskService.complete(taskId, Map.of(
        "approved", false,
        "rejectionReason", reason
    ));
  }
}
