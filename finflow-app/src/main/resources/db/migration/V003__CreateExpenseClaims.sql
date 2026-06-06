CREATE TABLE expense_claims (
    id UUID PRIMARY KEY,
    employee_id UUID NOT NULL,
    amount NUMERIC(19, 4) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    category VARCHAR(100) NOT NULL,
    description VARCHAR(2000),
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    submitted_at TIMESTAMP WITH TIME ZONE,
    cancelled_at TIMESTAMP WITH TIME ZONE,

    CONSTRAINT chk_expense_claims_amount_positive CHECK (amount > 0),
    CONSTRAINT chk_expense_claims_currency_length CHECK (char_length(currency) = 3)
);