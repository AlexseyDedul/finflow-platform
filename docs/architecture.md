# FinFlow Service Split Boundaries

## Current state

FinFlow is currently a modular monolith deployed as one Spring Boot application.

Main modules:

- Expense
- Workflow
- Ledger
- Document
- Reports
- Notifications
- Shared infrastructure

The current Kubernetes deployment validates the full runtime flow:

Employee JWT → Expense API → PostgreSQL → Outbox → LocalStack SQS → Consumer → Camunda workflow → Manager task.

## Target service boundaries

### expense-service

Owns:

- Expense claims
- Expense lifecycle: draft, submitted, cancelled
- ExpenseSubmitted domain event publication
- Employee ownership from JWT

Does not own:

- Workflow task execution
- Ledger postings
- Document binary storage internals

Database ownership:

- `expense_claims`
- Outbox table if service-local outbox is used

Publishes:

- `ExpenseSubmittedEvent`

### workflow-service

Owns:

- Approval workflow
- Camunda process execution
- Manager/finance review tasks

Consumes:

- `ExpenseSubmittedEvent`

Publishes:

- `ExpenseApprovedEvent`
- `ExpenseRejectedEvent`

Database ownership:

- Camunda schema
- Workflow read model tables if introduced

### ledger-service

Owns:

- Accounts
- Ledger entries
- Financial postings

Consumes:

- `ExpenseApprovedEvent`

Publishes:

- Optional `LedgerPostedEvent`

Database ownership:

- `ledger_accounts`
- `ledger_entries`

### document-service

Owns:

- Document metadata
- S3 object key lifecycle
- Upload/download contracts

Database ownership:

- `documents`

External dependencies:

- S3-compatible object storage

### report-service

Owns:

- Report jobs
- Report generation
- Report storage output

Reads from:

- Future query APIs or replicated read models

Avoids:

- Direct cross-service database joins

## Communication rules

Synchronous communication:

- Query-style reads only when necessary.
- No distributed transaction.
- Timeout and retry required for remote calls.

Asynchronous communication:

- Domain events through queue/topic.
- Outbox required for reliable event publication.
- Consumers must be idempotent.

## Security rules

- User identity comes from JWT.
- Client-controlled ownership fields are not trusted.
- Each service validates authorization locally.
- Internal service-to-service auth will be introduced before real production deployment.

## Migration order

1. Keep modular monolith stable.
2. Extract `document-service` or `report-service` first if low-risk extraction is desired.
3. Extract `workflow-service` if focusing on async/event boundaries.
4. Extract `ledger-service` only after event contracts are stable.
5. Keep `expense-service` as source of expense lifecycle truth.

## Non-goals for first split

- No distributed transaction.
- No shared writable database between services.
- No direct service-to-service entity sharing.
- No client-controlled employee ownership fields.