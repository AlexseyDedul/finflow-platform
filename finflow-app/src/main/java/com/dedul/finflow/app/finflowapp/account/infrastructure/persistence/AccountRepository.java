package com.dedul.finflow.app.finflowapp.account.infrastructure.persistence;

import com.dedul.finflow.app.finflowapp.account.domain.Account;
import com.dedul.finflow.app.finflowapp.account.domain.AccountType;
import com.dedul.finflow.app.finflowapp.account.domain.CurrencyCode;
import com.dedul.finflow.app.finflowapp.account.domain.Money;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class AccountRepository {
  private final AccountJpaRepository jpaRepository;

  public AccountRepository(AccountJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  public Account save(Account account) {
    AccountEntity saved = jpaRepository.save(toEntity(account));
    return toDomain(saved);
  }

  public Optional<Account> findById(UUID id) {
    return jpaRepository.findById(id).map(this::toDomain);
  }

  public List<Account> findAllByOwnerId(UUID ownerId) {
    return jpaRepository.findAllByOwnerId(ownerId).stream().map(this::toDomain).toList();
  }

  public boolean existsByOwnerIdAndTypeAndCurrency(
      UUID ownerId, AccountType type, String currency) {
    return jpaRepository.existsByOwnerIdAndTypeAndCurrency(ownerId, type, currency.toUpperCase());
  }

  private AccountEntity toEntity(Account account) {
    return new AccountEntity(
        account.id(),
        account.ownerId(),
        account.type(),
        account.status(),
        account.balance().amount(),
        account.balance().currency().value(),
        account.createdAt());
  }

  private Account toDomain(AccountEntity entity) {
    return Account.restore(
        entity.getId(),
        entity.getOwnerId(),
        entity.getType(),
        entity.getStatus(),
        new Money(entity.getBalance(), CurrencyCode.of(entity.getCurrency())),
        entity.getCreatedAt());
  }
}
