package com.dedul.finflow.app.finflowapp.workflow.application.delegate;

import com.dedul.finflow.app.finflowapp.expense.application.ExpenseService;
import com.dedul.finflow.app.finflowapp.notification.application.NotificationService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Slf4j
@Component("rejectExpenseDelegate")
@RequiredArgsConstructor
public class RejectExpenseDelegate implements JavaDelegate {

  private final ExpenseService expenseService;
  private final NotificationService notificationService;

  @Override
  public void execute(DelegateExecution execution) {
    UUID expenseId = UUID.fromString((String) execution.getVariable("expenseId"));
    String reason = (String) execution.getVariable("rejectionReason");

    expenseService.markRejected(expenseId, reason);

    var expense = expenseService.getDomainById(expenseId);

    expenseService.markRejected(expenseId, reason);

    notificationService
        .sendExpenseRejectedNotification(expense.employeeId(), expenseId, reason)
        .exceptionally(
            error -> {
              log.error("Async rejected notification failed: expenseId={}", expenseId, error);
              return null;
            });

    log.info(
        "Rejected expense from workflow: expenseId={}, reason={}, processInstanceId={}",
        expenseId,
        reason,
        execution.getProcessInstanceId());
  }
}
