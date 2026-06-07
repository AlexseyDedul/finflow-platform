package com.dedul.finflow.app.finflowapp.workflow.application;

import com.dedul.finflow.app.finflowapp.expense.application.event.ExpenseSubmittedEvent;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class WorkflowService {

  private static final String EXPENSE_APPROVAL_PROCESS_KEY = "expenseApprovalProcess";

  private final RuntimeService runtimeService;

  @Transactional
  public void createExpenseApproval(ExpenseSubmittedEvent expenseSubmittedEvent) {
    runtimeService.startProcessInstanceByKey(
        EXPENSE_APPROVAL_PROCESS_KEY,
        expenseSubmittedEvent.expenseId().toString(),
        Map.of(
            "expenseId", expenseSubmittedEvent.expenseId().toString(),
            "employeeId", expenseSubmittedEvent.employeeId().toString(),
            "amount", expenseSubmittedEvent.amount().toPlainString(),
            "currency", expenseSubmittedEvent.currency()
        )
    );
  }
}
