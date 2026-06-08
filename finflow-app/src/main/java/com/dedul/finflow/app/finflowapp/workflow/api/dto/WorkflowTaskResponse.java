package com.dedul.finflow.app.finflowapp.workflow.api.dto;

import java.util.UUID;

public record WorkflowTaskResponse(
    String taskId,
    String name,
    String processInstanceId,
    UUID expenseId
) {}
