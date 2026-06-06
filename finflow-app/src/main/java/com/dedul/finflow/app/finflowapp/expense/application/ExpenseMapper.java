package com.dedul.finflow.app.finflowapp.expense.application;

import com.dedul.finflow.app.finflowapp.expense.api.dto.ExpenseResponse;
import com.dedul.finflow.app.finflowapp.expense.domain.ExpenseClaim;
import org.springframework.stereotype.Component;

@Component
public class ExpenseMapper {
  public ExpenseResponse toResponse(ExpenseClaim expense) {
    return new ExpenseResponse(
        expense.id(),
        expense.employeeId(),
        expense.amount().amount(),
        expense.amount().currency().value(),
        expense.category(),
        expense.description(),
        expense.status(),
        expense.createdAt(),
        expense.submittedAt(),
        expense.cancelledAt());
  }
}
