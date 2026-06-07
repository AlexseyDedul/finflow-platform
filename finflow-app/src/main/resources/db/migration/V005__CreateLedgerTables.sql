CREATE TABLE ledger_transactions (
     id UUID PRIMARY KEY,
     reference_id UUID NOT NULL,
     reference_type VARCHAR(100) NOT NULL,
     status VARCHAR(50) NOT NULL,
     created_at TIMESTAMP WITH TIME ZONE NOT NULL,

     CONSTRAINT uq_ledger_reference UNIQUE (reference_id, reference_type)
);

CREATE TABLE ledger_entries (
    id UUID PRIMARY KEY,
    transaction_id UUID NOT NULL,
    account_id UUID NOT NULL,
    direction VARCHAR(20) NOT NULL,
    amount NUMERIC(19, 4) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_ledger_entries_transaction
        FOREIGN KEY (transaction_id)
        REFERENCES ledger_transactions(id),

    CONSTRAINT chk_ledger_entry_amount_positive
        CHECK (amount > 0)
);

CREATE INDEX idx_ledger_entries_account_id ON ledger_entries(account_id);
CREATE INDEX idx_ledger_entries_transaction_id ON ledger_entries(transaction_id);