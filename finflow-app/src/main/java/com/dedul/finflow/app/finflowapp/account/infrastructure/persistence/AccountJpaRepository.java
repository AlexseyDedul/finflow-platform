package com.dedul.finflow.app.finflowapp.account.infrastructure.persistence;

import com.dedul.finflow.app.finflowapp.account.domain.AccountType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface AccountJpaRepository extends JpaRepository<AccountEntity, UUID> {
  List<AccountEntity> findAllByOwnerId(UUID ownerId);

  Optional<AccountEntity> findByOwnerIdAndTypeAndCurrency(
      UUID ownerId, AccountType type, String currency);

  boolean existsByOwnerIdAndTypeAndCurrency(UUID ownerId, AccountType type, String currency);
}
