package com.dedul.finflow.app.finflowapp.reporting.application;

import com.dedul.finflow.app.finflowapp.expense.domain.ExpenseClaim;
import com.dedul.finflow.app.finflowapp.expense.infrastructure.persistence.ExpenseClaimRepository;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MonthlyExpenseReportGenerator {

  private final ExpenseClaimRepository expenseClaimRepository;

  public String generate(String month) {
    YearMonth yearMonth = YearMonth.parse(month);

    var from = yearMonth.atDay(1).atStartOfDay().toInstant(ZoneOffset.UTC);

    var to = yearMonth.plusMonths(1).atDay(1).atStartOfDay().toInstant(ZoneOffset.UTC);

    List<ExpenseClaim> expenses = expenseClaimRepository.findCreatedBetween(from, to);

    StringBuilder csv = new StringBuilder();

    csv.append("month,totalExpenses,totalAmount,currency\n");

    Map<String, List<ExpenseClaim>> byCurrency =
        expenses.stream()
            .collect(Collectors.groupingBy(expense -> expense.amount().currency().value()));

    byCurrency.entrySet().stream()
        .sorted(Map.Entry.comparingByKey())
        .forEach(
            entry -> {
              String currency = entry.getKey();
              List<ExpenseClaim> currencyExpenses = entry.getValue();

              BigDecimal total =
                  currencyExpenses.stream()
                      .map(expense -> expense.amount().amount())
                      .reduce(BigDecimal.ZERO, BigDecimal::add);

              csv.append(month)
                  .append(',')
                  .append(currencyExpenses.size())
                  .append(',')
                  .append(total)
                  .append(',')
                  .append(currency)
                  .append('\n');
            });

    csv.append('\n');
    csv.append("expenseId,employeeId,status,category,amount,currency,createdAt,description\n");

    expenses.stream()
        .sorted(Comparator.comparing(ExpenseClaim::createdAt))
        .forEach(
            expense ->
                csv.append(expense.id())
                    .append(',')
                    .append(expense.employeeId())
                    .append(',')
                    .append(expense.status())
                    .append(',')
                    .append(expense.category())
                    .append(',')
                    .append(expense.amount().amount())
                    .append(',')
                    .append(expense.amount().currency().value())
                    .append(',')
                    .append(expense.createdAt())
                    .append(',')
                    .append(escapeCsv(expense.description()))
                    .append('\n'));

    return csv.toString();
  }

  private String escapeCsv(String value) {
    if (value == null) {
      return "";
    }

    boolean mustQuote =
        value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r");

    String escaped = value.replace("\"", "\"\"");

    return mustQuote ? "\"" + escaped + "\"" : escaped;
  }
}
