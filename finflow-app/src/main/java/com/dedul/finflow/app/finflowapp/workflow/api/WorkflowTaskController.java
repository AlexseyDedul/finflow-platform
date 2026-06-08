package com.dedul.finflow.app.finflowapp.workflow.api;

import com.dedul.finflow.app.finflowapp.workflow.api.dto.WorkflowTaskResponse;
import com.dedul.finflow.app.finflowapp.workflow.application.WorkflowTaskService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workflow/tasks")
@RequiredArgsConstructor
public class WorkflowTaskController {

  private final WorkflowTaskService workflowTaskService;

  @GetMapping
  public List<WorkflowTaskResponse> getOpenTasks() {
    return workflowTaskService.getOpenTasks();
  }

  @PostMapping("/{taskId}/approve")
  public void approve(@PathVariable String taskId) {
    workflowTaskService.approve(taskId);
  }

  @PostMapping("/{taskId}/reject")
  public void reject(@PathVariable String taskId, @RequestBody RejectWorkflowTaskRequest request) {
    workflowTaskService.reject(taskId, request.reason());
  }

  public record RejectWorkflowTaskRequest(String reason) {}
}
