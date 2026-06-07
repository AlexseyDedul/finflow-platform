package com.dedul.finflow.app.finflowapp.workflow.application.delegate;

import com.dedul.finflow.app.finflowapp.expense.application.ExpenseService;
import com.dedul.finflow.app.finflowapp.expense.domain.ExpenseClaim;
import com.dedul.finflow.app.finflowapp.ledger.application.LedgerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component("postLedgerDelegate")
@RequiredArgsConstructor
public class PostLedgerDelegate implements JavaDelegate {
  private final LedgerService ledgerService;
  private final ExpenseService expenseService;

  @Value("${app.ledger.accounts.expense-account-id}")
  private UUID expenseAccountId;

  @Value("${app.ledger.accounts.employee-payable-account-id}")
  private UUID employeePayableAccountId;

  @Override
  public void execute(DelegateExecution execution) {
    UUID expenseId = UUID.fromString((String) execution.getVariable("expenseId"));
    ExpenseClaim expense = expenseService.getDomainById(expenseId);

    ledgerService.postExpenseApproval(
        expense.id(),
        employeePayableAccountId,
        expenseAccountId,
        expense.amount().amount(),
        expense.amount().currency().value()
    );

    expenseService.markApproved(expenseId);

    log.info(
        "Posted ledger transaction for expense: expenseId={}, processInstanceId={}",
        expenseId,
        execution.getProcessInstanceId()
    );
  }
}
