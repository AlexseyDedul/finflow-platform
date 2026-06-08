package com.dedul.finflow.app.finflowapp.workflow.application;

import com.dedul.finflow.app.finflowapp.expense.application.event.ExpenseSubmittedEvent;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.dedul.finflow.app.finflowapp.workflow.api.dto.WorkflowTaskResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowService {

  private static final String EXPENSE_APPROVAL_PROCESS_KEY = "expenseApprovalProcess";

  private final RuntimeService runtimeService;
  private final TaskService taskService;

  @Transactional
  public void startExpenseApproval(ExpenseSubmittedEvent expenseSubmittedEvent) {
    String buisnessKey = expenseSubmittedEvent.expenseId().toString();

    long activeInstances = runtimeService
            .createProcessInstanceQuery()
            .processDefinitionKey(EXPENSE_APPROVAL_PROCESS_KEY)
            .processInstanceBusinessKey(buisnessKey)
            .active()
            .count();

    if (activeInstances > 0) {
      log.info(
          "Expense approval workflow already active, skipping: expenseId={}",
          expenseSubmittedEvent.expenseId()
      );
      return;
    }

    runtimeService.startProcessInstanceByKey(
        EXPENSE_APPROVAL_PROCESS_KEY,
        expenseSubmittedEvent.expenseId().toString(),
        Map.of(
            "expenseId", expenseSubmittedEvent.expenseId().toString(),
            "employeeId", expenseSubmittedEvent.employeeId().toString(),
            "amount", expenseSubmittedEvent.amount().toPlainString(),
            "currency", expenseSubmittedEvent.currency()));

    log.info("Started expense approval workflow: expenseId={}", expenseSubmittedEvent.expenseId());
  }
}
