package com.dedul.finflow.app.finflowapp.expense.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.dedul.finflow.app.finflowapp.expense.api.dto.CreateExpenseRequest;
import com.dedul.finflow.app.finflowapp.expense.api.dto.ExpenseResponse;
import com.dedul.finflow.app.finflowapp.expense.domain.ExpenseCategory;
import com.dedul.finflow.app.finflowapp.expense.domain.ExpenseClaim;
import com.dedul.finflow.app.finflowapp.expense.domain.ExpenseStatus;
import com.dedul.finflow.app.finflowapp.expense.infrastructure.persistence.ExpenseClaimRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

  @Mock private ExpenseClaimRepository expenseRepository;

  @Mock private ExpenseMapper expenseMapper;

  @InjectMocks private ExpenseService expenseService;

  @Test
  void create_shouldUseAuthenticatedEmployeeIdInsteadOfRequestEmployeeId() {
    UUID authenticatedEmployeeId = UUID.fromString("00000000-0000-0000-0000-000000000001");

    CreateExpenseRequest request =
        new CreateExpenseRequest(
            new BigDecimal("123.45"), "EUR", ExpenseCategory.TRAVEL, "Security check taxi");

    when(expenseRepository.save(any(ExpenseClaim.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    when(expenseMapper.toResponse(any(ExpenseClaim.class)))
        .thenReturn(
            new ExpenseResponse(
                UUID.randomUUID(),
                authenticatedEmployeeId,
                new BigDecimal("123.45"),
                "EUR",
                ExpenseCategory.TRAVEL,
                "Security check taxi",
                ExpenseStatus.DRAFT,
                Instant.now(),
                null,
                null));

    ExpenseResponse response = expenseService.create(authenticatedEmployeeId, request);

    assertThat(response.employeeId()).isEqualTo(authenticatedEmployeeId);

    ArgumentCaptor<ExpenseClaim> captor = ArgumentCaptor.forClass(ExpenseClaim.class);
    org.mockito.Mockito.verify(expenseRepository).save(captor.capture());

    ExpenseClaim savedExpense = captor.getValue();

    assertThat(savedExpense.employeeId()).isEqualTo(authenticatedEmployeeId);
  }
}
