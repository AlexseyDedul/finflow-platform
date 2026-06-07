package com.dedul.finflow.app.finflowapp.workflow.api;

import com.dedul.finflow.app.finflowapp.workflow.api.dto.WorkflowTaskResponse;
import com.dedul.finflow.app.finflowapp.workflow.application.WorkflowTaskService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
  public void reject(
          @PathVariable String taskId,
          @RequestBody RejectWorkflowTaskRequest request
  ) {
    workflowTaskService.reject(taskId, request.reason());
  }

  public record RejectWorkflowTaskRequest(String reason) {
  }
}
