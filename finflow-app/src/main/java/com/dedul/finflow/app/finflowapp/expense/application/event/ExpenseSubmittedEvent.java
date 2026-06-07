package com.dedul.finflow.app.finflowapp.expense.application.event;

import java.math.BigDecimal;
import java.util.UUID;

public record ExpenseSubmittedEvent(
    UUID expenseId, UUID employeeId, BigDecimal amount, String currency) {}
