package com.dedul.finflow.app.finflowapp.expense.application;

import com.dedul.finflow.app.finflowapp.account.domain.CurrencyCode;
import com.dedul.finflow.app.finflowapp.account.domain.Money;
import com.dedul.finflow.app.finflowapp.expense.api.dto.CreateExpenseRequest;
import com.dedul.finflow.app.finflowapp.expense.api.dto.ExpenseResponse;
import com.dedul.finflow.app.finflowapp.expense.application.event.ExpenseSubmittedEvent;
import com.dedul.finflow.app.finflowapp.expense.domain.ExpenseClaim;
import com.dedul.finflow.app.finflowapp.expense.domain.ExpenseStatus;
import com.dedul.finflow.app.finflowapp.expense.infrastructure.persistence.ExpenseClaimRepository;
import com.dedul.finflow.app.finflowapp.shared.exception.BusinessRuleViolationException;
import com.dedul.finflow.app.finflowapp.shared.exception.NotFoundException;
import com.dedul.finflow.app.finflowapp.shared.outbox.OutboxService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExpenseService {
  private final ExpenseClaimRepository expenseRepository;
  private final ExpenseMapper expenseMapper;
  private final OutboxService outboxService;

  @Transactional
  public ExpenseResponse create(CreateExpenseRequest request) {
    ExpenseClaim expense =
        ExpenseClaim.createDraft(
            request.employeeId(),
            new Money(request.amount(), CurrencyCode.of(request.currency())),
            request.category(),
            request.description());

    ExpenseClaim saved = expenseRepository.save(expense);
    return expenseMapper.toResponse(saved);
  }

  @Transactional(readOnly = true)
  public ExpenseResponse getById(UUID id) {
    ExpenseClaim expense =
        expenseRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Expense claim not found: " + id));

    return expenseMapper.toResponse(expense);
  }

  @Transactional(readOnly = true)
  public ExpenseClaim getDomainById(UUID id) {
    return expenseRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Expense claim not found: " + id));
  }

  @Transactional(readOnly = true)
  public List<ExpenseResponse> getByEmployeeId(UUID employeeId) {
    return expenseRepository.findAllByEmployeeId(employeeId).stream()
        .map(expenseMapper::toResponse)
        .toList();
  }

  @Transactional(readOnly = true)
  public List<ExpenseResponse> getByEmployeeIdAndStatus(UUID employeeId, ExpenseStatus status) {
    return expenseRepository.findAllByEmployeeIdAndStatus(employeeId, status).stream()
        .map(expenseMapper::toResponse)
        .toList();
  }

  @Transactional
  public ExpenseResponse submit(UUID id) {
    ExpenseClaim expense =
        expenseRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Expense claim not found: " + id));

    try {
      expense.submit();
    } catch (IllegalStateException e) {
      throw new BusinessRuleViolationException(e.getMessage(), e);
    }

    ExpenseClaim saved = expenseRepository.save(expense);
    outboxService.saveEvent(
        saved.id(),
        "ExpenseSubmittedEvent",
        new ExpenseSubmittedEvent(
            saved.id(),
            saved.employeeId(),
            saved.amount().amount(),
            saved.amount().currency().value()));

    return expenseMapper.toResponse(saved);
  }

  @Transactional
  public ExpenseResponse cancel(UUID id) {
    ExpenseClaim expense =
        expenseRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Expense claim not found: " + id));
    try {
      expense.cancel();
    } catch (IllegalStateException e) {
      throw new BusinessRuleViolationException(e.getMessage(), e);
    }

    ExpenseClaim saved = expenseRepository.save(expense);
    return expenseMapper.toResponse(saved);
  }

  @Transactional(readOnly = true)
  public void ensureExpenseExists(UUID id) {
    if (expenseRepository.findById(id).isEmpty()) {
      throw new NotFoundException("Expense claim not found: " + id);
    }
  }

  @Transactional
  public void markApproved(UUID expenseId) {
    ExpenseClaim expense =
        expenseRepository
            .findById(expenseId)
            .orElseThrow(() -> new NotFoundException("Expense claim not found: " + expenseId));

    expense.approve();

    expenseRepository.save(expense);
  }

  @Transactional
  public void markRejected(UUID expenseId, String reason) {
    ExpenseClaim expense =
        expenseRepository
            .findById(expenseId)
            .orElseThrow(() -> new NotFoundException("Expense claim not found: " + expenseId));

    expense.reject(reason);

    expenseRepository.save(expense);
  }
}
