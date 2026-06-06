package com.dedul.finflow.app.finflowapp.expense.api.dto;

import com.dedul.finflow.app.finflowapp.expense.domain.ExpenseCategory;
import com.dedul.finflow.app.finflowapp.expense.domain.ExpenseStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ExpenseResponse(
    UUID id,
    UUID employeeId,
    BigDecimal amount,
    String currency,
    ExpenseCategory category,
    String description,
    ExpenseStatus status,
    Instant createdAt,
    Instant submittedAt,
    Instant cancelledAt) {}
