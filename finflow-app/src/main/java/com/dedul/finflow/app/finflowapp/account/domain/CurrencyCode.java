package com.dedul.finflow.app.finflowapp.account.domain;

import java.util.Currency;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public record CurrencyCode(String value) {
  private static final Set<String> SUPPORTED_CURRENCIES = Set.of("EUR", "USD", "PLN");

  public CurrencyCode {
    Objects.requireNonNull(value, "currency code must not be null");
    value = value.trim().toUpperCase(Locale.ROOT);

    if (value.length() != 3) {
      throw new IllegalArgumentException("Currency code must contain exactly 3 characters");
    }

    if (!isIsoCurrency(value)) {
      throw new IllegalArgumentException("Unknown ISO-4217 currency code: " + value);
    }

    if (!SUPPORTED_CURRENCIES.contains(value)) {
      throw new IllegalArgumentException("Unsupported currency code: " + value);
    }
  }

  public static CurrencyCode of(String value) {
    return new CurrencyCode(value);
  }

  public static CurrencyCode eur() {
    return new CurrencyCode("EUR");
  }

  public static CurrencyCode usd() {
    return new CurrencyCode("USD");
  }

  public static CurrencyCode pln() {
    return new CurrencyCode("PLN");
  }

  private static boolean isIsoCurrency(String value) {
    try {
      Currency.getInstance(value);
      return true;
    } catch (IllegalArgumentException ex) {
      return false;
    }
  }

  @Override
  public String toString() {
    return value;
  }
}
