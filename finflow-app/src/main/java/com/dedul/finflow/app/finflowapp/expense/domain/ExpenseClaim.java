package com.dedul.finflow.app.finflowapp.expense.domain;

import com.dedul.finflow.app.finflowapp.account.domain.Money;
import com.dedul.finflow.app.finflowapp.shared.exception.InvalidExpenseStateException;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class ExpenseClaim {
  private final UUID id;
  private final UUID employeeId;
  private final Money amount;
  private final ExpenseCategory category;
  private final String description;
  private ExpenseStatus status;
  private final Instant createdAt;
  private Instant submittedAt;
  private Instant cancelledAt;

  private ExpenseClaim(
      UUID id,
      UUID employeeId,
      Money amount,
      ExpenseCategory category,
      String description,
      ExpenseStatus status,
      Instant createdAt,
      Instant submittedAt,
      Instant cancelledAt) {
    this.id = Objects.requireNonNull(id, "id must not be null");
    this.employeeId = Objects.requireNonNull(employeeId, "employeeId must not be null");
    this.amount = Objects.requireNonNull(amount, "amount must not be null");
    this.category = Objects.requireNonNull(category, "category must not be null");
    this.description = normalizeDescription(description);
    this.status = Objects.requireNonNull(status, "status must not be null");
    this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
    this.submittedAt = submittedAt;
    this.cancelledAt = cancelledAt;
    if (!amount.isPositive()) {
      throw new IllegalArgumentException("Expense amount must be positive");
    }
  }

  public static ExpenseClaim createDraft(
      UUID employeeId, Money amount, ExpenseCategory category, String description) {
    return new ExpenseClaim(
        UUID.randomUUID(),
        employeeId,
        amount,
        category,
        description,
        ExpenseStatus.DRAFT,
        Instant.now(),
        null,
        null);
  }

  public static ExpenseClaim restore(
      UUID id,
      UUID employeeId,
      Money amount,
      ExpenseCategory category,
      String description,
      ExpenseStatus status,
      Instant createdAt,
      Instant submittedAt,
      Instant cancelledAt) {
    return new ExpenseClaim(
        id, employeeId, amount, category, description, status, createdAt, submittedAt, cancelledAt);
  }

  public void approve() {
    if (status != ExpenseStatus.SUBMITTED && status != ExpenseStatus.MANAGER_REVIEW) {
      throw new InvalidExpenseStateException("Only submitted expense can be approved");
    }

    status = ExpenseStatus.APPROVED;
  }

  public void submit() {
    if (status != ExpenseStatus.DRAFT) {
      throw new IllegalStateException("Only DRAFT expense can be submitted");
    }
    status = ExpenseStatus.SUBMITTED;
    submittedAt = Instant.now();
  }

  public void cancel() {
    if (status != ExpenseStatus.DRAFT) {
      throw new IllegalStateException("Only DRAFT expense can be cancelled");
    }
    status = ExpenseStatus.CANCELLED;
    cancelledAt = Instant.now();
  }

  public void reject(String reason) {
    if (status != ExpenseStatus.SUBMITTED && status != ExpenseStatus.MANAGER_REVIEW) {
      throw new InvalidExpenseStateException("Only submitted expense can be rejected. Reason: " + reason);
    }

    status = ExpenseStatus.REJECTED;
  }

  private static String normalizeDescription(String description) {
    if (description == null || description.isBlank()) {
      return null;
    }
    return description.trim();
  }

  public UUID id() {
    return id;
  }

  public UUID employeeId() {
    return employeeId;
  }

  public Money amount() {
    return amount;
  }

  public ExpenseCategory category() {
    return category;
  }

  public String description() {
    return description;
  }

  public ExpenseStatus status() {
    return status;
  }

  public Instant createdAt() {
    return createdAt;
  }

  public Instant submittedAt() {
    return submittedAt;
  }

  public Instant cancelledAt() {
    return cancelledAt;
  }
}
