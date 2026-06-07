package com.dedul.finflow.app.finflowapp.ledger.api.dto;

import com.dedul.finflow.app.finflowapp.ledger.domain.LedgerEntryDirection;
import com.dedul.finflow.app.finflowapp.ledger.domain.LedgerReferenceType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record PostLedgerTransactionRequest(
    @NotNull UUID referenceId,
    @NotNull LedgerReferenceType referenceType,
    @NotEmpty List<@Valid LedgerEntryRequest> entries) {

  public record LedgerEntryRequest(
      @NotNull UUID accountId,
      @NotNull LedgerEntryDirection direction,
      @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
      @NotBlank @Size(min = 3, max = 3) String currency) {}
}
