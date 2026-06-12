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
EMPLOYEE_WORKFLOW_STATUS=$(curl -s -o /dev/null -w "%{http_code}" "$APP_URL/api/workflow/tasks" \
  -H "Authorization: Bearer $EMPLOYEE_TOKEN")

if [ "$EMPLOYEE_WORKFLOW_STATUS" != "403" ]; then
  echo "Expected employee workflow status 403, got $EMPLOYEE_WORKFLOW_STATUS"
  exit 1
fi

echo "Creating expense with spoofed employeeId..."
CREATE_RESPONSE=$(curl -fsS -X POST "$APP_URL/api/expenses" \
  -H "Authorization: Bearer $EMPLOYEE_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "employeeId": "99999999-9999-9999-9999-999999999999",
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

echo "Waiting for async outbox/SQS/Camunda flow..."
sleep 8

echo "Getting manager token..."
MANAGER_TOKEN_RESPONSE=$(curl -fsS -X POST "$KEYCLOAK_URL/realms/finflow/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=finflow-app" \
  -d "grant_type=password" \
  -d "username=manager" \
  -d "password=manager")

MANAGER_TOKEN=$(echo "$MANAGER_TOKEN_RESPONSE" | jq -r '.access_token // empty')

TASKS=$(curl -fsS "$APP_URL/api/workflow/tasks" \
  -H "Authorization: Bearer $MANAGER_TOKEN")

echo "$TASKS" | jq .

FOUND=$(echo "$TASKS" | jq --arg expenseId "$EXPENSE_ID" '[.[] | select(.expenseId == $expenseId)] | length')

if [ "$FOUND" != "1" ]; then
  echo "Expected workflow task for expense $EXPENSE_ID, found $FOUND"
  exit 1
fi

echo "Smoke test passed."
