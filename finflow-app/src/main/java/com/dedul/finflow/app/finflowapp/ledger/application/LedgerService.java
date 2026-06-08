package com.dedul.finflow.app.finflowapp.ledger.application;

import com.dedul.finflow.app.finflowapp.ledger.api.dto.LedgerEntryResponse;
import com.dedul.finflow.app.finflowapp.ledger.api.dto.LedgerTransactionResponse;
import com.dedul.finflow.app.finflowapp.ledger.api.dto.PostLedgerTransactionRequest;
import com.dedul.finflow.app.finflowapp.ledger.domain.LedgerEntryDirection;
import com.dedul.finflow.app.finflowapp.ledger.domain.LedgerReferenceType;
import com.dedul.finflow.app.finflowapp.ledger.domain.LedgerTransaction;
import com.dedul.finflow.app.finflowapp.ledger.infrastructure.persistence.LedgerRepository;
import com.dedul.finflow.app.finflowapp.shared.exception.BusinessRuleViolationException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LedgerService {

  private final LedgerRepository ledgerRepository;
  private final LedgerMapper mapper;

  @Transactional
  public LedgerTransactionResponse post(PostLedgerTransactionRequest request) {
    if (ledgerRepository.existsByReference(request.referenceId(), request.referenceType())) {
      throw new BusinessRuleViolationException(
          "Ledger transaction already exists for reference: "
              + request.referenceType()
              + "/"
              + request.referenceId());
    }

    List<LedgerTransaction.PostLedgerEntryCommand> entries =
        request.entries().stream().map(mapper::toCommand).toList();

    LedgerTransaction transaction =
        LedgerTransaction.post(request.referenceId(), request.referenceType(), entries);

    return mapper.toResponse(ledgerRepository.save(transaction));
  }

  @Transactional(readOnly = true)
  public List<LedgerEntryResponse> getAccountEntries(UUID accountId) {
    return mapper.toEntryResponses(ledgerRepository.findEntriesByAccountId(accountId));
  }

  @Transactional
  public LedgerTransactionResponse postExpenseApproval(
      UUID expenseId,
      UUID employeePayableAccountId,
      UUID expenseAccountId,
      BigDecimal amount,
      String currency) {
    return ledgerRepository
        .findByReference(expenseId, LedgerReferenceType.EXPENSE_CLAIM)
        .map(mapper::toResponse)
        .orElseGet(
            () -> {
              PostLedgerTransactionRequest request =
                  new PostLedgerTransactionRequest(
                      expenseId,
                      LedgerReferenceType.EXPENSE_CLAIM,
                      List.of(
                          new PostLedgerTransactionRequest.LedgerEntryRequest(
                              expenseAccountId, LedgerEntryDirection.DEBIT, amount, currency),
                          new PostLedgerTransactionRequest.LedgerEntryRequest(
                              employeePayableAccountId,
                              LedgerEntryDirection.CREDIT,
                              amount,
                              currency)));
              return post(request);
            });
  }
}
