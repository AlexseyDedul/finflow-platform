package com.dedul.finflow.app.finflowapp.account.api;

import com.dedul.finflow.app.finflowapp.account.api.dto.AccountResponse;
import com.dedul.finflow.app.finflowapp.account.api.dto.CreateAccountRequest;
import com.dedul.finflow.app.finflowapp.account.application.AccountService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
  private final AccountService accountService;

  public AccountController(AccountService accountService) {
    this.accountService = accountService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public AccountResponse create(@Valid @RequestBody CreateAccountRequest request) {
    return accountService.create(request);
  }

  @GetMapping("/{id}")
  public AccountResponse getById(@PathVariable UUID id) {
    return accountService.getById(id);
  }

  @GetMapping("/owner/{ownerId}")
  public List<AccountResponse> getByOwnerId(@PathVariable UUID ownerId) {
    return accountService.getByOwnerId(ownerId);
  }

  @PatchMapping("/{id}/block")
  public AccountResponse block(@PathVariable UUID id) {
    return accountService.block(id);
  }

  @PatchMapping("/{id}/activate")
  public AccountResponse activate(@PathVariable UUID id) {
    return accountService.activate(id);
  }
}
