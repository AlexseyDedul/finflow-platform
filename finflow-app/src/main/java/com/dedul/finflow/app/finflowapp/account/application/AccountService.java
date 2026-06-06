package com.dedul.finflow.app.finflowapp.account.application;

import com.dedul.finflow.app.finflowapp.account.api.dto.AccountResponse;
import com.dedul.finflow.app.finflowapp.account.api.dto.CreateAccountRequest;
import com.dedul.finflow.app.finflowapp.account.domain.Account;
import com.dedul.finflow.app.finflowapp.account.domain.CurrencyCode;
import com.dedul.finflow.app.finflowapp.account.infrastructure.persistence.AccountRepository;
import com.dedul.finflow.app.finflowapp.shared.exception.BusinessRuleViolationException;
import com.dedul.finflow.app.finflowapp.shared.exception.NotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {
  private final AccountRepository accountRepository;
  private final AccountMapper accountMapper;

  public AccountService(AccountRepository accountRepository, AccountMapper accountMapper) {
    this.accountRepository = accountRepository;
    this.accountMapper = accountMapper;
  }

  @Transactional
  public AccountResponse create(CreateAccountRequest request) {
    String currency = request.currency().toUpperCase();
    boolean alreadyExists =
        accountRepository.existsByOwnerIdAndTypeAndCurrency(
            request.ownerId(), request.type(), currency);

    if (alreadyExists) {
      throw new BusinessRuleViolationException(
          "Account already exists for ownerId=%s, type=%s, currency=%s"
              .formatted(request.ownerId(), request.type(), currency));
    }

    Account account = Account.open(request.ownerId(), request.type(), CurrencyCode.of(currency));

    Account saved = accountRepository.save(account);
    return accountMapper.toResponse(saved);
  }

  @Transactional(readOnly = true)
  public AccountResponse getById(UUID id) {

    Account account =
        accountRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Account not found: " + id));

    return accountMapper.toResponse(account);
  }

  @Transactional(readOnly = true)
  public List<AccountResponse> getByOwnerId(UUID ownerId) {

    return accountRepository.findAllByOwnerId(ownerId).stream()
        .map(accountMapper::toResponse)
        .toList();
  }

  @Transactional
  public AccountResponse block(UUID id) {
    Account account =
        accountRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Account not found: " + id));

    try {
      account.block();
    } catch (IllegalStateException e) {
      throw new BusinessRuleViolationException(e.getMessage(), e);
    }

    Account saved = accountRepository.save(account);
    return accountMapper.toResponse(saved);
  }

  @Transactional
  public AccountResponse activate(UUID id) {
    Account account =
        accountRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Account not found: " + id));
    try {
      account.activate();
    } catch (IllegalStateException e) {
      throw new BusinessRuleViolationException(e.getMessage(), e);
    }

    Account saved = accountRepository.save(account);
    return accountMapper.toResponse(saved);
  }
}
