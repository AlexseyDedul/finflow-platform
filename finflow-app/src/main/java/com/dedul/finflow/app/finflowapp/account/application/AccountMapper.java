package com.dedul.finflow.app.finflowapp.account.application;

import com.dedul.finflow.app.finflowapp.account.api.dto.AccountResponse;
import com.dedul.finflow.app.finflowapp.account.domain.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {
  public AccountResponse toResponse(Account account) {
    return new AccountResponse(
        account.id(),
        account.ownerId(),
        account.type(),
        account.status(),
        account.balance().amount(),
        account.balance().currency().value(),
        account.createdAt());
  }
}
