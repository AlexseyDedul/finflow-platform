package com.dedul.finflow.app.finflowapp.expense.application;

import com.dedul.finflow.app.finflowapp.account.domain.CurrencyCode;
import com.dedul.finflow.app.finflowapp.account.domain.Money;
import com.dedul.finflow.app.finflowapp.expense.api.dto.CreateExpenseRequest;
import com.dedul.finflow.app.finflowapp.expense.api.dto.ExpenseResponse;
import com.dedul.finflow.app.finflowapp.expense.domain.ExpenseClaim;
import com.dedul.finflow.app.finflowapp.expense.domain.ExpenseStatus;
import com.dedul.finflow.app.finflowapp.expense.infrastructure.persistence.ExpenseClaimRepository;
import com.dedul.finflow.app.finflowapp.shared.exception.BusinessRuleViolationException;
import com.dedul.finflow.app.finflowapp.shared.exception.NotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExpenseService {
  private final ExpenseClaimRepository expenseRepository;
  private final ExpenseMapper expenseMapper;

  public ExpenseService(ExpenseClaimRepository expenseRepository, ExpenseMapper expenseMapper) {
    this.expenseRepository = expenseRepository;
    this.expenseMapper = expenseMapper;
  }

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
}
