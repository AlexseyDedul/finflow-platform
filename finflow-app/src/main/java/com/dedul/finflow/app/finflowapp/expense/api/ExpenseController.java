package com.dedul.finflow.app.finflowapp.expense.api;


import com.dedul.finflow.app.finflowapp.expense.api.dto.CreateExpenseRequest;
import com.dedul.finflow.app.finflowapp.expense.api.dto.ExpenseResponse;
import com.dedul.finflow.app.finflowapp.expense.application.ExpenseService;
import com.dedul.finflow.app.finflowapp.expense.domain.ExpenseStatus;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {
  private final ExpenseService expenseService;

  public ExpenseController(ExpenseService expenseService) {
    this.expenseService = expenseService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ExpenseResponse create(@Valid @RequestBody CreateExpenseRequest request) {
    return expenseService.create(request);
  }

  @GetMapping("/{id}")
  public ExpenseResponse getById(@PathVariable UUID id) {
    return expenseService.getById(id);
  }

  @GetMapping("/employee/{employeeId}")
  public List<ExpenseResponse> getByEmployeeId(
      @PathVariable UUID employeeId,
      @RequestParam(required = false) ExpenseStatus status
  ) {
    if (status != null) {
      return expenseService.getByEmployeeIdAndStatus(employeeId, status);
    }
    return expenseService.getByEmployeeId(employeeId);
  }

  @PostMapping("/{id}/submit")
  public ExpenseResponse submit(@PathVariable UUID id) {
    return expenseService.submit(id);
  }

  @PostMapping("/{id}/cancel")
  public ExpenseResponse cancel(@PathVariable UUID id) {
    return expenseService.cancel(id);
  }
}
