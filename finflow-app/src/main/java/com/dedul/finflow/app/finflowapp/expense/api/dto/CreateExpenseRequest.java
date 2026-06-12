package com.dedul.finflow.app.finflowapp.expense.api.dto;

import com.dedul.finflow.app.finflowapp.expense.domain.ExpenseCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CreateExpenseRequest(
    @NotNull @DecimalMin(value = "0.01", message = "amount must be greater than zero") @Digits(integer = 15, fraction = 4) BigDecimal amount,
    @NotNull @Pattern(
            regexp = "^[A-Z]{3}$",
            message = "currency must be ISO-4217 uppercase code, for example EUR")
        String currency,
    @NotNull ExpenseCategory category,
    @Size(max = 2000) String description) {}
