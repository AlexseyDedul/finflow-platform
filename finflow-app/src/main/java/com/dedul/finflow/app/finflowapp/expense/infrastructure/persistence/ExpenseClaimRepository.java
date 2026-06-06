package com.dedul.finflow.app.finflowapp.expense.infrastructure.persistence;

import com.dedul.finflow.app.finflowapp.account.domain.CurrencyCode;
import com.dedul.finflow.app.finflowapp.account.domain.Money;
import com.dedul.finflow.app.finflowapp.expense.domain.ExpenseClaim;
import com.dedul.finflow.app.finflowapp.expense.domain.ExpenseStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class ExpenseClaimRepository {

  private final ExpenseClaimJpaRepository jpaRepository;

  public ExpenseClaimRepository(ExpenseClaimJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  public ExpenseClaim save(ExpenseClaim expense) {
    ExpenseClaimEntity saved = jpaRepository.save(toEntity(expense));
    return toDomain(saved);
  }

  public Optional<ExpenseClaim> findById(UUID id) {
    return jpaRepository.findById(id).map(this::toDomain);
  }

  public List<ExpenseClaim> findAllByEmployeeId(UUID employeeId) {
    return jpaRepository.findAllByEmployeeIdOrderByCreatedAtDesc(employeeId).stream()
        .map(this::toDomain)
        .toList();
  }

  public List<ExpenseClaim> findAllByEmployeeIdAndStatus(UUID employeeId, ExpenseStatus status) {
    return jpaRepository
        .findAllByEmployeeIdAndStatusOrderByCreatedAtDesc(employeeId, status)
        .stream()
        .map(this::toDomain)
        .toList();
  }

  private ExpenseClaimEntity toEntity(ExpenseClaim expense) {
    return new ExpenseClaimEntity(
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

  private ExpenseClaim toDomain(ExpenseClaimEntity entity) {
    return ExpenseClaim.restore(
        entity.getId(),
        entity.getEmployeeId(),
        new Money(entity.getAmount(), CurrencyCode.of(entity.getCurrency())),
        entity.getCategory(),
        entity.getDescription(),
        entity.getStatus(),
        entity.getCreatedAt(),
        entity.getSubmittedAt(),
        entity.getCancelledAt());
  }
}
