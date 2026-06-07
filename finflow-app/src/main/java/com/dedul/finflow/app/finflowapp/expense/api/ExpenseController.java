package com.dedul.finflow.app.finflowapp.expense.api;

import com.dedul.finflow.app.finflowapp.document.api.dto.DocumentResponse;
import com.dedul.finflow.app.finflowapp.document.application.DocumentService;
import com.dedul.finflow.app.finflowapp.document.domain.DocumentOwnerType;
import com.dedul.finflow.app.finflowapp.expense.api.dto.CreateExpenseRequest;
import com.dedul.finflow.app.finflowapp.expense.api.dto.ExpenseResponse;
import com.dedul.finflow.app.finflowapp.expense.application.ExpenseService;
import com.dedul.finflow.app.finflowapp.expense.domain.ExpenseStatus;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {
  private final ExpenseService expenseService;
  private final DocumentService documentService;

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
      @PathVariable UUID employeeId, @RequestParam(required = false) ExpenseStatus status) {
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

  @PostMapping(value = "/{id}/receipt", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public DocumentResponse uploadReceipt(
      @PathVariable UUID id, @RequestPart("file") MultipartFile file) {
    expenseService.ensureExpenseExists(id);
    return documentService.upload(id, DocumentOwnerType.EXPENSE_CLAIM, file);
  }
}
