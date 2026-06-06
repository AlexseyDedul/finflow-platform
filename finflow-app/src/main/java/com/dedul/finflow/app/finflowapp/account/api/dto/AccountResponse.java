package com.dedul.finflow.app.finflowapp.account.api.dto;

import com.dedul.finflow.app.finflowapp.account.domain.AccountStatus;
import com.dedul.finflow.app.finflowapp.account.domain.AccountType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AccountResponse(
    UUID id,
    UUID ownerId,
    AccountType type,
    AccountStatus status,
    BigDecimal balance,
    String currency,
    Instant createdAt) {}
