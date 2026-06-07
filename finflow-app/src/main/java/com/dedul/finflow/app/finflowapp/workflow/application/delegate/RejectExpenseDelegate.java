package com.dedul.finflow.app.finflowapp.workflow.application.delegate;

import com.dedul.finflow.app.finflowapp.expense.application.ExpenseService;
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

  @Override
  public void execute(DelegateExecution execution) {
    UUID expenseId = UUID.fromString((String) execution.getVariable("expenseId"));
    String reason = (String) execution.getVariable("rejectionReason");

    expenseService.markRejected(expenseId, reason);

    log.info(
        "Rejected expense from workflow: expenseId={}, reason={}, processInstanceId={}",
        expenseId,
        reason,
        execution.getProcessInstanceId()
    );
  }
}
