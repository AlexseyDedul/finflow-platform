#!/usr/bin/env bash
set -euo pipefail

APP_URL="${APP_URL:-http://localhost:8080}"
KEYCLOAK_URL="${KEYCLOAK_URL:-http://localhost:8180}"

echo "Checking health..."
curl -fsS "$APP_URL/actuator/health" | jq .

echo "Getting employee token..."
EMPLOYEE_TOKEN_RESPONSE=$(curl -fsS -X POST "$KEYCLOAK_URL/realms/finflow/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=finflow-app" \
  -d "grant_type=password" \
  -d "username=employee" \
  -d "password=employee")

EMPLOYEE_TOKEN=$(echo "$EMPLOYEE_TOKEN_RESPONSE" | jq -r '.access_token // empty')

if [ -z "$EMPLOYEE_TOKEN" ]; then
  echo "Employee token was not issued"
  exit 1
fi

echo "Checking employee forbidden workflow access..."
EMPLOYEE_WORKFLOW_RESPONSE_FILE=$(mktemp)

EMPLOYEE_WORKFLOW_STATUS=$(curl -s -o "$EMPLOYEE_WORKFLOW_RESPONSE_FILE" -w "%{http_code}" "$APP_URL/api/workflow/tasks" \
  -H "Authorization: Bearer $EMPLOYEE_TOKEN")

if [ "$EMPLOYEE_WORKFLOW_STATUS" != "403" ]; then
  echo "Expected employee workflow status 403, got $EMPLOYEE_WORKFLOW_STATUS"
  cat "$EMPLOYEE_WORKFLOW_RESPONSE_FILE"
  exit 1
fi

EMPLOYEE_WORKFLOW_ERROR=$(jq -r '.error // empty' "$EMPLOYEE_WORKFLOW_RESPONSE_FILE")

if [ "$EMPLOYEE_WORKFLOW_ERROR" != "FORBIDDEN" ]; then
  echo "Expected FORBIDDEN error body, got:"
  cat "$EMPLOYEE_WORKFLOW_RESPONSE_FILE"
  exit 1
fi

rm -f "$EMPLOYEE_WORKFLOW_RESPONSE_FILE"

echo "Creating expense as authenticated employee..."
CREATE_RESPONSE=$(curl -fsS -X POST "$APP_URL/api/expenses" \
  -H "Authorization: Bearer $EMPLOYEE_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 123.45,
    "currency": "EUR",
    "category": "TRAVEL",
    "description": "K8s automated smoke check taxi"
  }')

EXPENSE_ID=$(echo "$CREATE_RESPONSE" | jq -r '.id')
OWNER_ID=$(echo "$CREATE_RESPONSE" | jq -r '.employeeId')

if [ "$OWNER_ID" != "00000000-0000-0000-0000-000000000001" ]; then
  echo "Expected owner from JWT, got $OWNER_ID"
  exit 1
fi

echo "Submitting expense: $EXPENSE_ID"
curl -fsS -X POST "$APP_URL/api/expenses/$EXPENSE_ID/submit" \
  -H "Authorization: Bearer $EMPLOYEE_TOKEN" | jq .

echo "Getting manager token..."
MANAGER_TOKEN_RESPONSE=$(curl -fsS -X POST "$KEYCLOAK_URL/realms/finflow/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=finflow-app" \
  -d "grant_type=password" \
  -d "username=manager" \
  -d "password=manager")

MANAGER_TOKEN=$(echo "$MANAGER_TOKEN_RESPONSE" | jq -r '.access_token // empty')

if [ -z "$MANAGER_TOKEN" ]; then
  echo "Manager token was not issued"
  exit 1
fi

echo "Waiting for async outbox/SQS/Camunda flow..."

FOUND="0"
TASKS="[]"

for attempt in $(seq 1 30); do
  TASKS=$(curl -fsS "$APP_URL/api/workflow/tasks" \
    -H "Authorization: Bearer $MANAGER_TOKEN")

  FOUND=$(echo "$TASKS" | jq --arg expenseId "$EXPENSE_ID" '[.[] | select(.expenseId == $expenseId)] | length')

  if [ "$FOUND" = "1" ]; then
    break
  fi

  echo "Workflow task not found yet for expense $EXPENSE_ID, attempt $attempt/30"
  sleep 2
done

echo "$TASKS" | jq .

if [ "$FOUND" != "1" ]; then
  echo "Expected workflow task for expense $EXPENSE_ID, found $FOUND"
  echo "Recent app logs:"
  kubectl logs -n finflow deployment/finflow-app --tail=120 || true
  exit 1
fi

echo "Checking validation error response..."
VALIDATION_RESPONSE_FILE=$(mktemp)

VALIDATION_STATUS=$(curl -s -o "$VALIDATION_RESPONSE_FILE" -w "%{http_code}" -X POST "$APP_URL/api/expenses" \
  -H "Authorization: Bearer $EMPLOYEE_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 0,
    "currency": "",
    "description": ""
  }')

if [ "$VALIDATION_STATUS" != "400" ]; then
  echo "Expected validation status 400, got $VALIDATION_STATUS"
  cat "$VALIDATION_RESPONSE_FILE"
  exit 1
fi

VALIDATION_PROBLEM_STATUS=$(jq -r '.status // empty' "$VALIDATION_RESPONSE_FILE")
VALIDATION_PROBLEM_TITLE=$(jq -r '.title // empty' "$VALIDATION_RESPONSE_FILE")
VALIDATION_PROBLEM_INSTANCE=$(jq -r '.instance // empty' "$VALIDATION_RESPONSE_FILE")

if [ "$VALIDATION_PROBLEM_STATUS" != "400" ]; then
  echo "Expected validation problem status 400, got:"
  cat "$VALIDATION_RESPONSE_FILE"
  exit 1
fi

if [ "$VALIDATION_PROBLEM_TITLE" != "Validation failed" ]; then
  echo "Expected validation problem title 'Validation failed', got:"
  cat "$VALIDATION_RESPONSE_FILE"
  exit 1
fi

if [ "$VALIDATION_PROBLEM_INSTANCE" != "/api/expenses" ]; then
  echo "Expected validation problem instance /api/expenses, got:"
  cat "$VALIDATION_RESPONSE_FILE"
  exit 1
fi

rm -f "$VALIDATION_RESPONSE_FILE"

echo "Smoke test passed."
