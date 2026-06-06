ALTER TABLE accounts
    ADD CONSTRAINT chk_accounts_balance_non_negative
    CHECK (balance >= 0);

ALTER TABLE accounts
    ADD CONSTRAINT chk_accounts_currency_length
    CHECK (char_length(currency) = 3);