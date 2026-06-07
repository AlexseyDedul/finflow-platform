package com.dedul.finflow.app.finflowapp.ledger.application;

import com.dedul.finflow.app.finflowapp.ledger.api.dto.LedgerEntryResponse;
import com.dedul.finflow.app.finflowapp.ledger.api.dto.LedgerTransactionResponse;
import com.dedul.finflow.app.finflowapp.ledger.api.dto.PostLedgerTransactionRequest;
import com.dedul.finflow.app.finflowapp.ledger.domain.LedgerEntry;
import com.dedul.finflow.app.finflowapp.ledger.domain.LedgerTransaction;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class LedgerMapper {

  public LedgerTransaction.PostLedgerEntryCommand toCommand(
      PostLedgerTransactionRequest.LedgerEntryRequest request) {
    return new LedgerTransaction.PostLedgerEntryCommand(
        request.accountId(), request.direction(), request.amount(), request.currency());
  }

  public LedgerTransactionResponse toResponse(LedgerTransaction transaction) {
    return new LedgerTransactionResponse(
        transaction.getId(),
        transaction.getReferenceId(),
        transaction.getReferenceType(),
        transaction.getStatus(),
        transaction.getCreatedAt(),
        transaction.getEntries().stream().map(this::toEntryResponse).toList());
  }

  public List<LedgerEntryResponse> toEntryResponses(List<LedgerEntry> entries) {
    return entries.stream().map(this::toEntryResponse).toList();
  }

  private LedgerEntryResponse toEntryResponse(LedgerEntry entry) {
    return new LedgerEntryResponse(
        entry.getId(),
        entry.getTransactionId(),
        entry.getAccountId(),
        entry.getDirection(),
        entry.getAmount(),
        entry.getCurrency(),
        entry.getCreatedAt());
  }
}
