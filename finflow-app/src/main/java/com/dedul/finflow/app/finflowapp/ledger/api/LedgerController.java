package com.dedul.finflow.app.finflowapp.ledger.api;

import com.dedul.finflow.app.finflowapp.ledger.api.dto.LedgerEntryResponse;
import com.dedul.finflow.app.finflowapp.ledger.api.dto.LedgerTransactionResponse;
import com.dedul.finflow.app.finflowapp.ledger.api.dto.PostLedgerTransactionRequest;
import com.dedul.finflow.app.finflowapp.ledger.application.LedgerService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ledger")
@RequiredArgsConstructor
public class LedgerController {

  private final LedgerService ledgerService;

  @PostMapping("/transactions")
  @ResponseStatus(HttpStatus.CREATED)
  public LedgerTransactionResponse post(@Valid @RequestBody PostLedgerTransactionRequest request) {
    return ledgerService.post(request);
  }

  @GetMapping("/accounts/{accountId}/entries")
  public List<LedgerEntryResponse> getAccountEntries(@PathVariable UUID accountId) {
    return ledgerService.getAccountEntries(accountId);
  }
}
