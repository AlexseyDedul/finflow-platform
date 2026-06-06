package com.dedul.finflow.app.finflowapp.account.api.dto;

import com.dedul.finflow.app.finflowapp.account.domain.AccountType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.UUID;

public record CreateAccountRequest(
    @NotNull UUID ownerId,
    @NotNull AccountType type,
    @NotNull @Pattern(
            regexp = "^[A-Z]{3}$",
            message = "currency must be ISO-4217 uppercase code, for example EUR")
        String currency) {}
